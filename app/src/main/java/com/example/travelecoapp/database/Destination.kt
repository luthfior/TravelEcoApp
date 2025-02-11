package com.example.travelecoapp.database

data class Destination(
    var name: String? = null,
    var location: String? = null,
    var shortDescription: String? = null,
    var longDescription: String? = null,
    var photo_url: String? = null
)