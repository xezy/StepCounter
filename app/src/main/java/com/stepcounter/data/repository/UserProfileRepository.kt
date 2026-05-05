package com.stepcounter.data.repository

import com.stepcounter.data.database.daos.UserProfileDao
import com.stepcounter.data.database.entities.UserProfileEntity
import com.stepcounter.domain.model.Gender
import com.stepcounter.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getProfile(): Flow<UserProfile?> {
        return userProfileDao.getProfile().map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun getProfileSync(): UserProfile? {
        return userProfileDao.getProfileSync()?.toDomain()
    }

    suspend fun saveProfile(profile: UserProfile) {
        val entity = profile.toEntity()
        val existing = userProfileDao.getProfileSync()
        if (existing == null) {
            userProfileDao.insertProfile(entity)
        } else {
            userProfileDao.updateProfile(entity.copy(id = existing.id))
        }
    }

    suspend fun createDefaultProfile() {
        val defaultProfile = UserProfile.default()
        val entity = defaultProfile.toEntity()
        userProfileDao.insertProfile(entity)
    }
}

private fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        heightCm = heightCm,
        gender = Gender.valueOf(gender),
        calibratedStride = calibratedStride,
        dailyGoal = dailyGoal
    )
}

private fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        heightCm = heightCm,
        gender = gender.name,
        calibratedStride = calibratedStride,
        dailyGoal = dailyGoal
    )
}
