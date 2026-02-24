package com.example.securehomeplus.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val score: Int,
    val summary: String,
    val dateMillis: Long = System.currentTimeMillis()
)
