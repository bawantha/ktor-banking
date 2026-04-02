package com.example.modules

import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.partnerModule() {
    routing {
        // Include the consolidated partner routes
        partnerRoutes()
    }
}