package com.example.securehomeplus.data.database.entities

data class LocationEntity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: String // "Police Station" | "Fire Station" | "Hospital"
)