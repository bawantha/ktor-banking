package com.example.models

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    val db = KMongo.createClient(
        connectionString = System.getenv("MONGODB_URI") ?: "mongodb://localhost:27017"
    ).coroutine
        .getDatabase("fingenius")
}