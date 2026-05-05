package com.stepcounter.core.sensor

import android.hardware.SensorEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

enum class PhonePosition {
    POCKET,
    HAND,
    BAG,
    UNKNOWN
}

class PhonePositionDetector {
    private val _position = MutableStateFlow(PhonePosition.UNKNOWN)
    val position: StateFlow<PhonePosition> = _position.asStateFlow()

    private val recentX = CircularBuffer(30)
    private val recentY = CircularBuffer(30)
    private val recentZ = CircularBuffer(30)

    fun analyze(event: SensorEvent) {
        recentX.add(abs(event.values[0]))
        recentY.add(abs(event.values[1]))
        recentZ.add(abs(event.values[2]))

        if (recentX.size() < 10) return

        val varX = calculateVariance(recentX)
        val varY = calculateVariance(recentY)
        val varZ = calculateVariance(recentZ)
        val totalVariance = varX + varY + varZ

        val detected = when {
            varZ > varX * 1.5f && totalVariance > 2.0f -> PhonePosition.POCKET
            totalVariance > 3.0f && varX > 0.5f && varY > 0.5f -> PhonePosition.HAND
            totalVariance < 1.0f -> PhonePosition.BAG
            else -> PhonePosition.UNKNOWN
        }

        _position.value = detected
    }

    fun getPositionMultiplier(): Float = when (_position.value) {
        PhonePosition.POCKET -> 1.0f
        PhonePosition.HAND -> 1.2f
        PhonePosition.BAG -> 1.5f
        PhonePosition.UNKNOWN -> 1.3f
    }

    private fun calculateVariance(buffer: CircularBuffer): Float {
        return buffer.variance()
    }
}
