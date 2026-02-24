package com.example.securehomeplus.data.repository

import android.content.Context
import com.example.securehomeplus.data.database.AppDatabase
import com.example.securehomeplus.data.database.entities.SecurityFactor

class SecurityRepository(private val dao: com.example.securehomeplus.data.database.dao.SecurityDao?) {

    private fun defaultQuestions(): List<SecurityFactor> {
        return listOf(
            SecurityFactor(id = 1, question = "Are all doors locked?", weight = 10, iconResName = "ic_lock"),
            SecurityFactor(id = 2, question = "Do you have CCTV installed?", weight = 15, iconResName = "ic_cctv"),
            SecurityFactor(id = 3, question = "Is there a fire extinguisher?", weight = 12, iconResName = "ic_fire_extinguisher"),
            SecurityFactor(id = 4, question = "Are all windows secured with grills/locks?", weight = 10, iconResName = "ic_window"),
            SecurityFactor(id = 5, question = "Is the main entry well-lit at night?", weight = 8, iconResName = "ic_light"),
            SecurityFactor(id = 6, question = "Do you have an emergency contact list visible?", weight = 5, iconResName = "ic_contacts"),
            SecurityFactor(id = 7, question = "Is the gas pipeline valve accessible and labeled?", weight = 10, iconResName = "ic_gas"),
            SecurityFactor(id = 8, question = "Do you regularly test smoke detectors?", weight = 10, iconResName = "ic_smoke_detector")
        )
    }

    suspend fun getQuestions(): List<SecurityFactor> {
        return try {
            if (dao != null) {
                val fromDb = dao.getAll()
                if (fromDb.isNotEmpty()) fromDb else defaultQuestions()
            } else {
                defaultQuestions()
            }
        } catch (ex: Exception) {
            defaultQuestions()
        }
    }

    suspend fun prepopulateIfEmpty() {
        if (dao == null) return
        val existing = dao.getAll()
        if (existing.isEmpty()) {
            dao.insertAll(defaultQuestions())
        }
    }
}
