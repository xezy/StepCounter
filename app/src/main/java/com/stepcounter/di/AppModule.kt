package com.stepcounter.di

import android.content.Context
import android.hardware.SensorManager
import androidx.room.Room
import com.stepcounter.core.location.LocationTracker
import com.stepcounter.core.sensor.AccelerometerProcessor
import com.stepcounter.core.sensor.PhonePositionDetector
import com.stepcounter.core.sensor.StepDetector
import com.stepcounter.core.session.SessionManager
import com.stepcounter.core.stride.StrideCalculator
import com.stepcounter.data.database.StepDatabase
import com.stepcounter.data.database.daos.DailySummaryDao
import com.stepcounter.data.database.daos.StepSessionDao
import com.stepcounter.data.database.daos.UserProfileDao
import com.stepcounter.data.repository.StepRepository
import com.stepcounter.data.repository.UserProfileRepository
import com.stepcounter.domain.model.Gender
import com.stepcounter.domain.model.UserProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    @Singleton
    fun provideLocationTracker(@ApplicationContext context: Context): LocationTracker {
        return LocationTracker(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StepDatabase {
        return Room.databaseBuilder(
            context,
            StepDatabase::class.java,
            "step_counter_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: StepDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideStepSessionDao(database: StepDatabase): StepSessionDao {
        return database.stepSessionDao()
    }

    @Provides
    @Singleton
    fun provideDailySummaryDao(database: StepDatabase): DailySummaryDao {
        return database.dailySummaryDao()
    }

    @Provides
    @Singleton
    fun provideStepRepository(
        stepSessionDao: StepSessionDao,
        dailySummaryDao: DailySummaryDao
    ): StepRepository {
        return StepRepository(stepSessionDao, dailySummaryDao)
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(userProfileDao: UserProfileDao): UserProfileRepository {
        return UserProfileRepository(userProfileDao)
    }

    @Provides
    @Singleton
    fun provideStrideCalculator(): StrideCalculator {
        return StrideCalculator()
    }

    @Provides
    @Singleton
    fun provideAccelerometerProcessor(): AccelerometerProcessor {
        return AccelerometerProcessor()
    }

    @Provides
    @Singleton
    fun providePhonePositionDetector(): PhonePositionDetector {
        return PhonePositionDetector()
    }

    @Provides
    fun provideStepDetector(strideCalculator: StrideCalculator): StepDetector {
        val initialStride = strideCalculator.getInitialStride()
        val height = 170f
        return StepDetector(userHeight = height, gender = Gender.MALE)
    }

    @Provides
    @Singleton
    fun provideSessionManager(strideCalculator: StrideCalculator): SessionManager {
        return SessionManager(strideCalculator)
    }
}
