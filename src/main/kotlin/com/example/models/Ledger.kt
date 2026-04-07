package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class LedgerItem(
    val date: String,
    val instrumentNo: String,
    val reference: String,
    val description: String,
    @Serializable(with = BigDecimalSerializer::class)
    val quantity: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val debit: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val credit: java.math.BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val balance: java.math.BigDecimal,
)

@Serializable
data class LedgerJson(
    val partner: Partner?,
    val ledgerItems: List<LedgerItem>
)