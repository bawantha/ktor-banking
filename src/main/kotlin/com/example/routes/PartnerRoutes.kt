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
    }
        }
}