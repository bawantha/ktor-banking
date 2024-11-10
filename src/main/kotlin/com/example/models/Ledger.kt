package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class LedgerItem(
    val date: String,
    val instrumentNo: String,
    val reference: String,
    val description: String,
    val quantity: String,
    val debit: String,
    val credit: String,
    val balance: String,
)

@Serializable
data class LedgerJson(
    val partner: Partner?,
    val ledgerItems: List<LedgerItem>
)