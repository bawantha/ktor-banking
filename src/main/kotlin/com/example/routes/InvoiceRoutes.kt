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
        post {
            try {
                val invoice = call.receive<Invoice>()

                // Insert the new invoice into the MongoDB collection
                val insertResult = invoicesCollection.insertOne(invoice)

                if (insertResult.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, "Invoice added successfully.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add invoice.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid data format.")
            }
        }
        // Route to get invoice(s)
        // Route to get invoice(s)
        get {
            val type = call.request.queryParameters["type"]
            val id = call.request.queryParameters["id"]

            try {
                if (id != null) {
                    val invoice = invoicesCollection.findOneById(id)

                    if (invoice != null) {
                        // Extract the partnerId from the invoice
                        val partnerId = invoice.partnerId

                        // Use partnerId to fetch the corresponding partner
                        val partner = partnersCollection.findOneById(partnerId)

                        if (partner != null) {
                            // Include the partner information in the response
                            val partnerJson = Json.encodeToString(partner)
                            val invoiceJson = Json.encodeToString(invoice)
                            val jsonResponse = """{"partner": $partnerJson, "invoice": $invoiceJson}"""

                            // Respond with a map containing both objects
                            call.respondText(jsonResponse, ContentType.Application.Json)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Partner not found.")
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Invoice not found.")
                    }
                } else {
                    val invoices: List<Invoice>

                    if (type != null) {
                        // Fetch all invoices of the specified type
                        invoices = invoicesCollection.find(Invoice::type eq type).toList()
                    } else {
                        // Fetch all invoices if both id and type are null
                        invoices = invoicesCollection.find().toList()
                    }

                    // Create a list to store the results with partner information
                    val responseList = mutableListOf<InvoiceJson>()

                    for (invoice in invoices) {
                        // Retrieve the partner for each invoice based on partnerId
                        val partner = partnersCollection.findOneById(invoice.partnerId)

                        val jsonResponse = InvoiceJson(partner, invoice)

                        // Add each JsonResponse object to the list
                        responseList.add(jsonResponse)
                    }

                    val jsonResponse = Json.encodeToString(responseList)
                    call.respond(HttpStatusCode.OK, jsonResponse)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve invoice(s).")
            }
        }
    }
}