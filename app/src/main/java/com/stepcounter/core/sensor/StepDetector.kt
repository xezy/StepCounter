package com.stepcounter.core.sensor

import com.stepcounter.domain.model.Gender
import com.stepcounter.domain.model.StepEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.abs

class StepDetector(
    private val userHeight: Float,
    private val gender: Gender
) {
    private val _stepEvents = MutableSharedFlow<StepEvent>(extraBufferCapacity = 1)
    val stepEvents = _stepEvents.asSharedFlow()

    private val buffer = CircularBuffer(20)
    private var lastStepTime = 0L
    private var refractoryPeriod = 300L
    private var isAboveThreshold = false

    private val baseThreshold = calculateBaseThreshold()

    fun processSignal(filteredValue: Float, timestamp: Long, positionMultiplier: Float) {
        buffer.add(abs(filteredValue))

        val variance = buffer.variance()
        val dynamicThreshold = baseThreshold * positionMultiplier * (1.0f + variance * 0.5f)
        val lowerThreshold = dynamicThreshold * 0.6f

        val timeSinceLastStep = timestamp - lastStepTime

        if (filteredValue > dynamicThreshold && timeSinceLastStep > refractoryPeriod && !isAboveThreshold) {
            isAboveThreshold = true
        } else if (filteredValue < lowerThreshold && isAboveThreshold) {
            val confidence = calculateConfidence(filteredValue, dynamicThreshold)
            val stepEvent = StepEvent(timestamp = timestamp, confidence = confidence)
            _stepEvents.tryEmit(stepEvent)
            isAboveThreshold = false
            lastStepTime = timestamp
            updateRefractoryPeriod(variance)
        }
    }

    private fun calculateBaseThreshold(): Float {
        val heightFactor = (userHeight / 100f) * 0.2f
        val genderFactor = if (gender == Gender.MALE) 0.0f else 0.1f
        return 0.8f + heightFactor + genderFactor
    }

    private fun calculateConfidence(value: Float, threshold: Float): Float {
        val ratio = value / threshold
        return minOf(1.0f, ratio * 0.5f).coerceAtLeast(0.3f)
    }

    private fun updateRefractoryPeriod(variance: Float) {
        refractoryPeriod = when {
            variance > 5.0f -> 250L
            variance < 2.0f -> 350L
            else -> 300L
        }
    }

    fun reset() {
        lastStepTime = 0L
        isAboveThreshold = false
    }
}
