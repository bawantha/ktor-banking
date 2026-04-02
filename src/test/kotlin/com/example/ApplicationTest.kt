package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import com.example.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
        }
        client.get("/json/kotlinx-serialization").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"hello\":\"world\"}", bodyAsText())
        }
    }
}
