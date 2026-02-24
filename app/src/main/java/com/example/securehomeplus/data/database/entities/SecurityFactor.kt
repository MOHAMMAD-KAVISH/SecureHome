package com.example.securehomeplus.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_factors")
data class SecurityFactor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val weight: Int = 10,
    val iconResName: String? = null
)
