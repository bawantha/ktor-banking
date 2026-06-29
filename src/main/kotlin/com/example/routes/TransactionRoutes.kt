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
        // Route to get transaction(s)
        get {
            val type = call.request.queryParameters["type"]
            val id = call.request.queryParameters["id"]

            try {
                if (id != null) {
                    val transaction = transactionsCollection.findOneById(id)

                    if (transaction != null) {
                        val partnerId = when (transaction.type) {
                            "BPV", "CPV" -> transaction.paymentTo
                            "BRV", "CRV" -> transaction.receiptFrom
                            else -> null
                        }
                        val partner = partnerId?.takeIf { it.isNotBlank() && it != "null" }?.let { partnersCollection.findOneById(it) }

                        val jsonResponse = Json.encodeToString(TransactionJson(partner, transaction))
                        call.respondText(jsonResponse, ContentType.Application.Json)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Transaction not found.")
                    }
                } else {
                    val transactions = when (type) {
                        "payment" -> transactionsCollection.find(
                            or(
                                Transaction::type eq "BPV",
                                Transaction::type eq "CPV"
                            )
                        ).toList()
                        "receipt" -> transactionsCollection.find(
                            or(
                                Transaction::type eq "BRV",
                                Transaction::type eq "CRV"
                            )
                        ).toList()
                        "journal" -> transactionsCollection.find(
                            Transaction::type eq "JV"
                        ).toList()
                        null -> transactionsCollection.find().toList()
                        else -> return@get call.respond(HttpStatusCode.BadRequest, "Invalid Type")
                    }

                    val responseList = mutableListOf<TransactionJson>()
                    for (transaction in transactions) {
                        val partnerId = when (transaction.type) {
                            "BPV", "CPV" -> transaction.paymentTo
                            "BRV", "CRV" -> transaction.receiptFrom
                            else -> null
                        }
                        val partner = partnerId?.takeIf { it.isNotBlank() && it != "null" }?.let { partnersCollection.findOneById(it) }

                        responseList.add(TransactionJson(partner, transaction))
                    }
                    val jsonResponse = Json.encodeToString(responseList)
                    call.respond(HttpStatusCode.OK, jsonResponse)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve transaction(s).")
            }
        }
    }
}