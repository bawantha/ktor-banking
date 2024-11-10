package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val _id: String? = null,
    val name: String,
    val coreCompany: String,
    val rate: String,
    val status: String,
    val taxExempted: String,
    val salesTax: String,
    val notes: String
)