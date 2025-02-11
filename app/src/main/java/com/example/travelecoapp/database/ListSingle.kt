package com.example.travelecoapp.database

data class ListSingle(
    val programs: List<SinglePrograms>,
    val reference: String? = null
)

data class SinglePrograms(
    val activity_name: String? = null,
    val activity_type: String? = null,
    val details: String? = null,
    val duration: String? = null,
    val id: String? = null,
    val inclussion: String? = null,
    val level: String? = null,
    val nett_price: String? = null,
    val photo_url: String? = null,
    val publish_price: String? = null
)