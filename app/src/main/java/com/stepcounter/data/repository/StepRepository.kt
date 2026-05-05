package com.stepcounter.data.repository

import com.stepcounter.data.database.daos.DailySummaryDao
import com.stepcounter.data.database.daos.StepSessionDao
import com.stepcounter.data.database.entities.DailySummaryEntity
import com.stepcounter.data.database.entities.StepSessionEntity
import com.stepcounter.domain.model.DailySummary
import com.stepcounter.domain.model.WalkSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepository @Inject constructor(
    private val stepSessionDao: StepSessionDao,
    private val dailySummaryDao: DailySummaryDao
) {
    fun getTodaySteps(): Flow<Int> {
        val (start, end) = getTodayRange()
        return stepSessionDao.getTodaySteps(start, end).map { it ?: 0 }
    }

    fun getDailyHistory(days: Int): Flow<List<DailySummary>> {
        return dailySummaryDao.getDailyHistory(days).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getRecentSessions(limit: Int = 10): Flow<List<WalkSession>> {
        return stepSessionDao.getRecentSessions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveSession(session: WalkSession) {
        val entity = session.toEntity()
        stepSessionDao.insertSession(entity)
        updateDailySummary(session)
    }

    private suspend fun updateDailySummary(session: WalkSession) {
        val date = timestampToDate(session.startTime)
        val (start, end) = getDateRange(date)
        val sessions = stepSessionDao.getTodaySessions(start, end)

        val totalSteps = sessions.sumOf { it.steps }
        val totalDistance = sessions.sumOf { it.gpsDistanceMeters.toDouble() }.toFloat()
        val goalAchieved = totalSteps >= 10000

        val summary = DailySummaryEntity(
            date = date,
            totalSteps = totalSteps,
            totalDistance = totalDistance,
            goalAchieved = goalAchieved,
            sessionCount = sessions.size
        )
        dailySummaryDao.upsertSummary(summary)
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val today = LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        return getDateRange(today)
    }

    private fun getDateRange(date: String): Pair<Long, Long> {
        val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        val startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return Pair(startOfDay, endOfDay)
    }

    private fun timestampToDate(timestamp: Long): String {
        return LocalDate.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

private fun StepSessionEntity.toDomain(): WalkSession {
    return WalkSession(
        id = id,
        startTime = startTime,
        endTime = endTime,
        steps = steps,
        gpsDistanceMeters = gpsDistanceMeters,
        avgStride = avgStride,
        hasGps = hasGps,
        isPaused = isPaused,
        pauseDurationMs = pauseDurationMs
    )
}

private fun WalkSession.toEntity(): StepSessionEntity {
    return StepSessionEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        steps = steps,
        gpsDistanceMeters = gpsDistanceMeters,
        avgStride = avgStride,
        hasGps = hasGps,
        isPaused = isPaused,
        pauseDurationMs = pauseDurationMs
    )
}

private fun DailySummaryEntity.toDomain(): DailySummary {
    return DailySummary(
        date = date,
        totalSteps = totalSteps,
        totalDistance = totalDistance,
        goalAchieved = goalAchieved,
        sessionCount = sessionCount
    )
}
