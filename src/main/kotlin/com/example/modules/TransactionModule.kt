package com.examplemodules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.exampleroutes.transactionRoutes

fun Application.transactionModule() {
    routing {
        // Include the consolidated transaction routes
        transactionRoutes()
    }
}