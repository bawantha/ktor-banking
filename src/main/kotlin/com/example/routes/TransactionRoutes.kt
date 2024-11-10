package com.exampleroutes

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
    val database = Database()
    val transactionsCollection = database.db.getCollection<Transaction>("transactions")
    val partnersCollection = database.db.getCollection<Partner>("partners")

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
        // Route to get all transactions
        get {
            val type = call.request.queryParameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing Type")
            try {
                // Fetch all sales invoices
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
                    else -> {
                        // If no valid type parameter is provided, return a BadRequest
                        return@get call.respond(HttpStatusCode.BadRequest, "Invalid Type")
                    }
                }
                val responseList = mutableListOf<TransactionJson>()
                for (transaction in transactions) {
                    // Retrieve the partner for each invoice based on partnerId
                    val partner = when (type) {
                        "payment" -> {
                            partnersCollection.findOneById(transaction.paymentTo)
                        }
                        "receipt" -> {
                            partnersCollection.findOneById(transaction.receiptFrom)
                        }
                    }
                    val jsonResponse = TransactionJson(partner, transaction)
                    // Add each JsonResponse object to the list
                    responseList.add(jsonResponse)
                }
                // Respond with the list of invoices with their corresponding partners
                val jsonResponse = Json.encodeToString(responseList)
                call.respond(HttpStatusCode.OK, jsonResponse)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve ${type}s.")
            }
        }
    }
}