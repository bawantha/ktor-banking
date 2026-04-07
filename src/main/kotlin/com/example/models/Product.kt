package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val _id: String? = null,
    val name: String,
    val coreCompany: String,
    @Serializable(with = BigDecimalSerializer::class)
    val rate: java.math.BigDecimal,
    val status: String,
    val taxExempted: String,
    @Serializable(with = BigDecimalSerializer::class)
    val salesTax: java.math.BigDecimal,
    val notes: String
)