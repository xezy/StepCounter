package com.stepcounter.domain.model

data class UserProfile(
    val heightCm: Float,
    val gender: Gender,
    val calibratedStride: Float,
    val dailyGoal: Int
) {
    companion object {
        fun default(): UserProfile = UserProfile(
            heightCm = 170f,
            gender = Gender.MALE,
            calibratedStride = 0f,
            dailyGoal = 10000
        )
    }
}
