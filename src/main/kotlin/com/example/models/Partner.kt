package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Partner(
    val _id: String? = null,
    val type: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val status: String,
    val contactNo: String,
    val openingBalance: String,
    val address: String,
    val repName: String,
    val repContact: String,
    val repDesignation: String
)