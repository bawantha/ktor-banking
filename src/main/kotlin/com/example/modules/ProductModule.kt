package com.example.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.*

fun Application.productModule() {
    routing {
        // Include the consolidated product routes
        productRoutes()
    }
}
