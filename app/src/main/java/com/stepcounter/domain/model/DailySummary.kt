package com.stepcounter.domain.model

data class DailySummary(
    val date: String,
    val totalSteps: Int,
    val totalDistance: Float,
    val goalAchieved: Boolean,
    val sessionCount: Int
)
