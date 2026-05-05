package com.stepcounter.core.sensor

import kotlin.math.sqrt

class CircularBuffer(private val capacity: Int = 20) {
    private val buffer = FloatArray(capacity)
    private var index = 0
    private var count = 0

    fun add(value: Float) {
        buffer[index % capacity] = value
        index++
        if (count < capacity) count++
    }

    fun variance(): Float {
        if (count < 2) return 0f
        val mean = buffer.take(count).average().toFloat()
        val squaredDiffs = buffer.take(count).map { (it - mean) * (it - mean) }
        return squaredDiffs.sum() / count
    }

    fun mean(): Float {
        if (count == 0) return 0f
        return buffer.take(count).average().toFloat()
    }

    fun size(): Int = count
}

object MagnitudeCalculator {
    fun calculate(x: Float, y: Float, z: Float): Float {
        return sqrt(x * x + y * y + z * z)
    }
}
