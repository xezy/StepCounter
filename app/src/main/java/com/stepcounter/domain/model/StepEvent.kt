package com.stepcounter.domain.model

data class StepEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val confidence: Float
)
