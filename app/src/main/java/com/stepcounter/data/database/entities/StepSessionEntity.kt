package com.stepcounter.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_sessions")
data class StepSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long?,
    val steps: Int,
    val gpsDistanceMeters: Float,
    val avgStride: Float,
    val hasGps: Boolean,
    val isPaused: Boolean,
    val pauseDurationMs: Long = 0
)
