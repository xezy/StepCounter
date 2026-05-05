package com.stepcounter.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stepcounter.data.database.entities.StepSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StepSessionEntity): Long

    @Update
    suspend fun updateSession(session: StepSessionEntity)

    @Query("SELECT * FROM step_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime DESC")
    fun getSessionsByDate(startOfDay: Long, endOfDay: Long): Flow<List<StepSessionEntity>>

    @Query("SELECT * FROM step_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    suspend fun getTodaySessions(startOfDay: Long, endOfDay: Long): List<StepSessionEntity>

    @Query("SELECT SUM(steps) FROM step_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    fun getTodaySteps(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("DELETE FROM step_sessions WHERE endTime < :cutoffTime")
    suspend fun deleteOldSessions(cutoffTime: Long)

    @Query("SELECT * FROM step_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<StepSessionEntity>>
}
