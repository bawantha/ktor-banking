package com.example.models


import kotlinx.serialization.Serializable

@Serializable
data class InvoiceItem(
    val productName: String,
    @Serializable(with = BigDecimalSerializer::class)
    val quantity: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val rate: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val valueOfSupplies: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val salesTax: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val netAmount: java.math.BigDecimal,
)

@Serializable
data class Invoice(
    val _id: String? = null,
    val type: String,
    val invoiceDate: String,
    val dueDate: String,
    val invoiceNo: String,
    val partnerId: String,
    val creditTerm: String,
    val reference: String,
    @Serializable(with = BigDecimalSerializer::class)
    val invoiceTotal: java.math.BigDecimal,
    val invoiceItems: List<InvoiceItem>,
)

@Serializable
data class InvoiceJson(
    val partner: Partner?,
    val invoice: Invoice
)