package com.examplemodules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.exampleroutes.ledgerRoutes

fun Application.ledgerModule() {
    routing {
        // Include the consolidated customer routes
        ledgerRoutes()
    }
}
