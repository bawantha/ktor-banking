package com.exampleroutes

import com.example.models.Partner
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.and
import com.example.models.Database

fun Route.partnerRoutes(){
    val database = Database()
    val partnersCollection = database.db.getCollection<Partner>("partners")

    route("/partners"){
        // Route to add a partner
        post {
            try {
                val partner = call.receive<Partner>()

                // Insert the new partner into the MongoDB collection
                val insertResult = partnersCollection.insertOne(partner)

                if (insertResult.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, "${partner.type.replaceFirstChar { it.uppercase() }} added successfully: ${partner.firstName}  ${partner.lastName}.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add ${partner.type}.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid input data format.")
            }
            put("{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")

                try {
                    val updatedPartner = call.receive<Partner>()

                    // Update the partner in the MongoDB collection
                    val filter = Partner::_id eq id
                    val updateResult = partnersCollection.replaceOne(filter, updatedPartner)

                    if (updateResult.matchedCount > 0) {
                        call.respond(HttpStatusCode.OK, "Partner updated successfully.")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Partner not found.")
                    }
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid input data format.")
                }
            }

            get {
                val type = call.request.queryParameters["type"]
                val id = call.request.queryParameters["id"]
                val statusParam = call.request.queryParameters["status"]
                try {
                    if (id != null) {
                        // Retrieve a partner by ID (ignoring the 'type' parameter)
                        val partner = partnersCollection.findOneById(id)
                        if (partner != null) {
                            call.respond(partner)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Partner not found.")
                        }
                    } else if (type != null) {
                        // No ID parameter, retrieve partners based on type and status
                        val partners: List<Partner> =
                            if (statusParam != null) {
                                partnersCollection.find(and(Partner::type eq type, Partner::status eq statusParam)).toList()
                            } else {
                                partnersCollection.find(Partner::type eq type).toList()
                            }
                        call.respond(HttpStatusCode.OK, partners)
                    } else {
                        // Retrieve all partners
                        val partners = partnersCollection.find().toList()
                        call.respond(HttpStatusCode.OK, partners)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve partners.")
                }
            }
    }
        }
}