package com.stepcounter.core.location

import kotlin.math.*

data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

class GpsCalibrator {
    private val points = mutableListOf<GpsPoint>()
    private var totalDistance = 0.0
    private var isActive = false

    fun addPoint(fix: GpsFix) {
        val point = GpsPoint(
            latitude = fix.latitude,
            longitude = fix.longitude,
            timestamp = fix.timestamp
        )

        if (points.isNotEmpty()) {
            val lastPoint = points.last()
            val distance = calculateHaversineDistance(
                lastPoint.latitude, lastPoint.longitude,
                point.latitude, point.longitude
            )
            totalDistance += distance
        }

        points.add(point)
        isActive = true
    }

    fun getTotalDistanceMeters(): Double {
        return totalDistance
    }

    fun calibrateStride(totalSteps: Int): Float {
        if (totalSteps == 0 || totalDistance == 0.0) return 0f
        return (totalDistance / totalSteps).toFloat()
    }

    fun reset() {
        points.clear()
        totalDistance = 0.0
        isActive = false
    }

    private fun calculateHaversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
