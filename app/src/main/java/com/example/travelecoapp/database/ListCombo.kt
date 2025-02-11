package com.example.travelecoapp.database

data class ListCombo(
    val programs: List<ComboPrograms>,
    val reference: String? = null
)

data class ComboPrograms(
    val activity_name: String? = null,
    val duration: String? = null,
    val inclussion: String? = null,
    val level: String? = null,
    val nett_price: String? = null,
    val photo_url: String? = null,
    val publish_price: String? = null,
    val start_time: String? = null
)
