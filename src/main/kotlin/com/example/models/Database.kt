package com.example.models

class Database {
    val db = KMongo.createClient(
        connectionString = "<YOUR_CONNECTION_STRING>"
    ).coroutine
        .getDatabase("fingenius")
}