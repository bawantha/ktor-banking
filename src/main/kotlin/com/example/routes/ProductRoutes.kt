package com.exampleroutes

import com.example.models.Product
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import com.example.models.Database

fun Route.productRoutes(){
    val database = Database()
    val productsCollection = database.db.getCollection<Product>("products")

    route("/products"){
        // Route to add a product
        post {
            try {
                val product = call.receive<Product>()

                // Insert the new product into the MongoDB collection
                val insertResult = productsCollection.insertOne(product)

                if (insertResult.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, "Product added successfully.")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add product.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product data format.")
            }
        }
        // Route to edit a product
        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing ID")

            try {
                val updatedProduct = call.receive<Product>()

                // Update the product in the MongoDB collection
                val filter = Product::_id eq id
                val updateResult = productsCollection.replaceOne(filter, updatedProduct)

                if (updateResult.matchedCount > 0) {
                    call.respond(HttpStatusCode.OK, "Product updated successfully.")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Product not found.")
                }
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid product data format.")
            }
        }
        // Route to get product(s)
    }
}