package com.stepcounter.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summary")
data class DailySummaryEntity(
    @PrimaryKey
    val date: String,
    val totalSteps: Int,
    val totalDistance: Float,
    val goalAchieved: Boolean,
    val sessionCount: Int
)
