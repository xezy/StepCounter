package com.stepcounter.core.sensor

import android.hardware.SensorEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.sqrt

class AccelerometerProcessor {
    private val _filteredSignal = MutableSharedFlow<Float>(extraBufferCapacity = 1)
    val filteredSignal = _filteredSignal.asSharedFlow()

    private var previousFiltered = 0f
    private val alpha = 0.8f
    private var gravityInitialized = false
    private var gravityMagnitude = 0f

    fun processEvent(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val magnitude = MagnitudeCalculator.calculate(x, y, z)

        if (!gravityInitialized) {
            gravityMagnitude = magnitude
            gravityInitialized = true
            previousFiltered = 0f
        }

        val gravityRemoved = magnitude - gravityMagnitude
        val filtered = lowPassFilter(gravityRemoved)

        _filteredSignal.tryEmit(filtered)
    }

    private fun lowPassFilter(input: Float): Float {
        previousFiltered = alpha * previousFiltered + (1 - alpha) * input
        return previousFiltered
    }

    fun reset() {
        gravityInitialized = false
        previousFiltered = 0f
    }
}
