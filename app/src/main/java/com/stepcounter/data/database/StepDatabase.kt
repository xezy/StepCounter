package com.stepcounter.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stepcounter.data.database.converters.AppTypeConverters
import com.stepcounter.data.database.daos.DailySummaryDao
import com.stepcounter.data.database.daos.StepSessionDao
import com.stepcounter.data.database.daos.UserProfileDao
import com.stepcounter.data.database.entities.DailySummaryEntity
import com.stepcounter.data.database.entities.StepSessionEntity
import com.stepcounter.data.database.entities.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        StepSessionEntity::class,
        DailySummaryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(AppTypeConverters::class)
abstract class StepDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun stepSessionDao(): StepSessionDao
    abstract fun dailySummaryDao(): DailySummaryDao
}
