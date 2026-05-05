package com.stepcounter.core.session

import com.stepcounter.core.location.GpsCalibrator
import com.stepcounter.core.stride.StrideCalculator
import com.stepcounter.domain.model.WalkSession

class SessionManager(
    private val strideCalculator: StrideCalculator
) {
    private var currentSession: WalkSession? = null
    private val autoPauseHandler = AutoPauseHandler()
    private val gpsCalibrator = GpsCalibrator()
    private var recentStepsForGps = 0
    private var recentStepsWindowStart = 0L

    fun startSession(startTime: Long): WalkSession {
        currentSession = WalkSession(
            startTime = startTime,
            lastStepTime = startTime
        )
        recentStepsForGps = 0
        recentStepsWindowStart = startTime
        gpsCalibrator.reset()
        return currentSession!!
    }

    fun addStep(timestamp: Long) {
        val session = currentSession ?: startSession(timestamp)
        session.steps++
        session.lastStepTime = timestamp

        recentStepsForGps++

        val pauseState = autoPauseHandler.onStep(timestamp)
        when (pauseState) {
            AutoPauseState.PAUSED -> {
                session.isPaused = true
                session.pauseStartTime = timestamp
            }
            AutoPauseState.RESUMED -> {
                session.isPaused = false
                session.pauseDurationMs -= (timestamp - session.pauseStartTime)
            }
            AutoPauseState.NEW_SESSION_REQUIRED -> {
                endSession(timestamp)
                startSession(timestamp)
            }
            else -> {}
        }
    }

    fun shouldActivateGps(currentTime: Long): Boolean {
        val windowDuration = currentTime - recentStepsWindowStart
        return recentStepsForGps >= 5 && windowDuration <= 10000L
    }

    fun onGpsFix(timestamp: Long) {
        gpsCalibrator.addPoint(
            com.stepcounter.core.location.GpsFix(
                latitude = 0.0,
                longitude = 0.0,
                accuracy = 0f,
                speed = 0f,
                timestamp = timestamp
            )
        )
    }

    fun endSession(endTime: Long): WalkSession? {
        val session = currentSession ?: return null
        session.endTime = endTime

        val gpsDistance = gpsCalibrator.getTotalDistanceMeters().toFloat()
        session.gpsDistanceMeters = gpsDistance
        session.hasGps = gpsDistance > 0f

        if (gpsDistance > 0f && session.steps > 0) {
            val calibratedStride = gpsCalibrator.calibrateStride(session.steps)
            session.avgStride = calibratedStride
            strideCalculator.addCalibration(gpsDistance, session.steps)
        } else {
            session.avgStride = strideCalculator.getCurrentStride()
        }

        currentSession = null
        return session
    }

    fun getCurrentSession(): WalkSession? = currentSession

    fun checkAutoPause(currentTime: Long): AutoPauseState {
        return autoPauseHandler.checkPause(currentTime)
    }

    fun reset() {
        currentSession = null
        autoPauseHandler.reset()
        gpsCalibrator.reset()
        recentStepsForGps = 0
    }
}
