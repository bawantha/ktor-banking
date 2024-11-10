package com.example.models


import kotlinx.serialization.Serializable

@Serializable
data class InvoiceItem(
    val productName: String,
    val quantity: String,
    val rate: String,
    val valueOfSupplies: String,
    val salesTax: String,
    val netAmount: String,
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
    val invoiceTotal: String,
    val invoiceItems: List<InvoiceItem>,
)

@Serializable
data class InvoiceJson(
    val partner: Partner?,
    val invoice: Invoice
)