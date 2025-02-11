package com.example.travelecoapp.database

data class Receipt(
    val orderEmail: String? = null,
    val orderName: String? = null,
    val orderPhone: String? = null,
    val orderId: String? = null,
    val orderProgram: String? = null,
    val orderOfPeople: String? = null,
    val orderPrice: String? = null,
    val orderStatus: String? = null
)
