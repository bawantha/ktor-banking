package com.example.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.transactionRoutes

fun Application.transactionModule() {
    routing {
        // Include the consolidated transaction routes
        transactionRoutes()
    }
}