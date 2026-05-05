package com.stepcounter.core.stride

import com.stepcounter.domain.model.Gender

class StrideCalculator(
    private var heightCm: Float = 170f,
    private var gender: Gender = Gender.MALE,
    private var calibratedStride: Float = 0f
) {
    private val recentCalibrations = mutableListOf<Float>()
    private val maxCalibrations = 10

    fun getCurrentStride(): Float {
        return if (calibratedStride > 0f) calibratedStride else getInitialStride()
    }

    fun addCalibration(gpsDistance: Float, stepCount: Int) {
        if (stepCount == 0 || gpsDistance == 0f) return
        val newStride = gpsDistance / stepCount
        if (newStride in 0.3f..1.5f) {
            recentCalibrations.add(newStride)
            if (recentCalibrations.size > maxCalibrations) {
                recentCalibrations.removeAt(0)
            }
            calibratedStride = recentCalibrations.average().toFloat()
        }
    }

    fun getInitialStride(): Float {
        return GenderHeightCalibrator.calculateInitialStride(heightCm, gender)
    }

    fun updateUserProfile(heightCm: Float, gender: Gender) {
        this.heightCm = heightCm
        this.gender = gender
        if (recentCalibrations.isEmpty()) {
            calibratedStride = 0f
        }
    }

    fun setCalibratedStride(stride: Float) {
        this.calibratedStride = stride
    }

    fun getCalibratedStride(): Float = calibratedStride
}
