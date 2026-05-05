package com.stepcounter.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stepcounter.data.database.entities.DailySummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailySummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSummary(summary: DailySummaryEntity)

    @Query("SELECT * FROM daily_summary ORDER BY date DESC LIMIT :days")
    fun getDailyHistory(days: Int): Flow<List<DailySummaryEntity>>

    @Query("SELECT * FROM daily_summary WHERE date = :date")
    fun getTodaySummary(date: String): Flow<DailySummaryEntity?>

    @Query("DELETE FROM daily_summary WHERE date < :cutoffDate")
    suspend fun deleteOldSummaries(cutoffDate: String)
}
