package com.stepcounter.domain.model

data class WalkSession(
    val id: Long = 0,
    val startTime: Long,
    var endTime: Long? = null,
    var steps: Int = 0,
    var gpsDistanceMeters: Float = 0f,
    var avgStride: Float = 0f,
    var hasGps: Boolean = false,
    var isPaused: Boolean = false,
    var pauseDurationMs: Long = 0,
    var lastStepTime: Long = 0,
    var pauseStartTime: Long = 0
)
