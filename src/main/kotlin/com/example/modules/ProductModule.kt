package com.examplemodules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.exampleroutes.productRoutes

fun Application.productModule() {
    routing {
        // Include the consolidated product routes
        productRoutes()
    }
}
