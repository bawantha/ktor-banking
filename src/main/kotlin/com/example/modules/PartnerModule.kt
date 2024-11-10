package com.examplemodules

import com.exampleroutes.partnerRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.partnerModule() {
    routing {
        // Include the consolidated partner routes
        partnerRoutes()
    }
}