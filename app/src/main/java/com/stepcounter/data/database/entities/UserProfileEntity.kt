package com.stepcounter.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    val heightCm: Float,
    val gender: String,
    val calibratedStride: Float,
    val dailyGoal: Int,
    val createdAt: Long = System.currentTimeMillis()
)
