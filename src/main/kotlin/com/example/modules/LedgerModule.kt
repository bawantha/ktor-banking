package com.example.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.*

fun Application.ledgerModule() {
    routing {
        // Include the consolidated customer routes
        ledgerRoutes()
    }
}
