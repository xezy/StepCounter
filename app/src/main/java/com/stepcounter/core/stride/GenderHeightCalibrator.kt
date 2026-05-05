package com.stepcounter.core.stride

import com.stepcounter.domain.model.Gender

object GenderHeightCalibrator {
    fun calculateInitialStride(heightCm: Float, gender: Gender): Float {
        val factor = when (gender) {
            Gender.MALE -> 0.415f
            Gender.FEMALE -> 0.413f
        }
        return (heightCm / 100f) * factor
    }
}
