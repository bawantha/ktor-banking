package com.exampleroutes

import com.example.models.Invoice
import com.example.models.Partner
import com.example.models.InvoiceJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import com.example.models.Database
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.invoiceRoutes(){
    val database = Database()
    val invoicesCollection = database.db.getCollection<Invoice>("invoices")
    val partnersCollection = database.db.getCollection<Partner>("partners")

    route("/invoices"){
        // Route to add an invoice
        
        // Route to get invoice(s)
    }
}