package com.exampleroutes

import com.examplemodels.Product
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

        // Route to edit a product

        // Route to get product(s)
    }
}