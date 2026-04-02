package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        //allowHeader("MyCustomHeader")
        allowHeader(HttpHeaders.ContentType)

        //allowNonSimpleContentTypes = true
        allowHost("localhost:8080")
        allowHost("127.0.0.1:8080")
        allowCredentials = true
    }
}







