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

        // Route to get all transactions
    }
}