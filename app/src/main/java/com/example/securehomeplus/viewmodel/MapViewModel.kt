package com.example.securehomeplus.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securehomeplus.data.database.entities.LocationEntity
import kotlin.random.Random

class MapViewModel : ViewModel() {


    fun generateNearby(lat: Double, lng: Double): List<LocationEntity> {
        val types = listOf("Police Station", "Fire Station", "Hospital")
        val points = mutableListOf<LocationEntity>()
        for (i in 1..6) {
            val latOffset = Random.nextDouble(-0.006, 0.006)
            val lonOffset = Random.nextDouble(-0.006, 0.006)
            val type = types.random()
            val name = "$type $i"
            points.add(LocationEntity(name, lat + latOffset, lng + lonOffset, type))
        }
        return points
    }

    fun getSafetyIndexText(): String {
        val index = (55..92).random()
        val label = when {
            index > 80 -> "Safe"
            index > 65 -> "Moderate"
            else -> "Caution"
        }
        return "Area Safety Index: $label ($index%)"
    }
}
