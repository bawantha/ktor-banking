package com.examplemodules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.exampleroutes.invoiceRoutes

fun Application.invoiceModule() {
    routing {
        // Include the consolidated customer routes
        invoiceRoutes()
    }
}
