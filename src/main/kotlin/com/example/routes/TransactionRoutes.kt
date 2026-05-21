package com.example.routes

import com.example.models.Partner
import com.example.models.Transaction
import com.example.models.TransactionJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.or
import com.example.models.Database
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.transactionRoutes(){
    val transactionsCollection = Database.db.getCollection<Transaction>("transactions")
    val partnersCollection = Database.db.getCollection<Partner>("partners")

    route("/transactions") {
        // Route to add a transaction
        post {
            try {
                val transaction = call.receive<Transaction>()

                // Insert the new transaction into the MongoDB collection
                val insertResult = transactionsCollection.insertOne(transaction)

                if (insertResult.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, "Transaction added successfully.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add transaction.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid data format.")
            }
        }
        // Route to get transactions
        get {
            val type = call.request.queryParameters["type"]
            val id = call.request.queryParameters["id"]

            try {
                if (id != null) {
                    val transaction = transactionsCollection.findOneById(id)
                    if (transaction != null) {
                        // For a specific transaction, try to find a relevant partner
                        val partnerId = if (transaction.paymentTo.isNotEmpty()) transaction.paymentTo else transaction.receiptFrom
                        val partner = partnersCollection.findOneById(partnerId)
                        val jsonResponse = Json.encodeToString(TransactionJson(partner, transaction))
                        call.respondText(jsonResponse, ContentType.Application.Json)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Transaction not found.")
                    }
                } else {
                    // Fetch all transactions or filter by type
                    val transactions = when (type) {
                        "payment" -> {
                            // Fetch transactions with type "BPV" and "CPV"
                            transactionsCollection.find(
                                or(
                                    Transaction::type eq "BPV",
                                    Transaction::type eq "CPV"
                                )
                            ).toList()
                        }
                        "receipt" -> {
                            // Fetch transactions with type "BRV" and "CRV"
                            transactionsCollection.find(
                                or(
                                    Transaction::type eq "BRV",
                                    Transaction::type eq "CRV"
                                )
                            ).toList()
                        }
                        "journal" -> {
                            // Fetch transactions with type "JV"
                            transactionsCollection.find(Transaction::type eq "JV").toList()
                        }
                        null -> {
                            // Fetch all transactions if type is not provided
                            transactionsCollection.find().toList()
                        }
                        else -> {
                            // If no valid type parameter is provided, return a BadRequest
                            return@get call.respond(HttpStatusCode.BadRequest, "Invalid Type")
                        }
                    }

                    val responseList = mutableListOf<TransactionJson>()
                    for (transaction in transactions) {
                        // Retrieve the partner for each transaction
                        val partner = when (transaction.type) {
                            "BPV", "CPV" -> partnersCollection.findOneById(transaction.paymentTo)
                            "BRV", "CRV" -> partnersCollection.findOneById(transaction.receiptFrom)
                            "JV" -> {
                                // For journal vouchers, logic depends on setup. Here we use paymentTo if valid, else receiptFrom.
                                partnersCollection.findOneById(transaction.paymentTo) ?: partnersCollection.findOneById(transaction.receiptFrom)
                            }
                            else -> null
                        }

                        val jsonResponse = TransactionJson(partner, transaction)
                        // Add each JsonResponse object to the list
                        responseList.add(jsonResponse)
                    }
                    // Respond with the list of transactions with their corresponding partners
                    val jsonResponse = Json.encodeToString(responseList)
                    call.respond(HttpStatusCode.OK, jsonResponse)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve transactions.")
            }
        }
    }
}