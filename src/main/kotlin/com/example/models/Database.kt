package com.example.models

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class Database {
    val db = KMongo.createClient(
        connectionString = "mongodb://localhost:27017"
    ).coroutine
        .getDatabase("fingenius")
}