package com.exampleroutes

import com.example.models.Partner
import com.example.models.Invoice
import com.example.models.LedgerItem
import com.example.models.LedgerJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.models.Database
import com.example.models.Transaction
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.text.SimpleDateFormat
import org.litote.kmongo.eq
import org.litote.kmongo.or
import java.math.BigDecimal

fun Route.ledgerRoutes(){
    val database = Database()
    val invoicesCollection = database.db.getCollection<Invoice>("invoices")
    val transactionsCollection = database.db.getCollection<Transaction>("transactions")
    val partnersCollection = database.db.getCollection<Partner>("partners")

    route("/ledger"){
        // Route to get ledger items
    }
}