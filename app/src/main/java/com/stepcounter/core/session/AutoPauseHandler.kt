package com.stepcounter.core.session

class AutoPauseHandler(
    private val pauseDelayMs: Long = 60000L,
    private val resumeWindowMs: Long = 300000L
) {
    private var lastStepTime: Long = 0
    private var isPaused: Boolean = false
    private var pauseStartTime: Long = 0

    fun onStep(timestamp: Long): AutoPauseState {
        lastStepTime = timestamp

        if (isPaused) {
            val timeSincePause = timestamp - pauseStartTime
            if (timeSincePause <= resumeWindowMs) {
                isPaused = false
                return AutoPauseState.RESUMED
            } else {
                return AutoPauseState.NEW_SESSION_REQUIRED
            }
        }

        return AutoPauseState.ACTIVE
    }

    fun checkPause(currentTime: Long): AutoPauseState {
        if (isPaused || lastStepTime == 0L) return AutoPauseState.ACTIVE

        val timeSinceLastStep = currentTime - lastStepTime
        if (timeSinceLastStep >= pauseDelayMs && !isPaused) {
            isPaused = true
            pauseStartTime = currentTime
            return AutoPauseState.PAUSED
        }

        return AutoPauseState.ACTIVE
    }

    fun isCurrentlyPaused(): Boolean = isPaused

    fun reset() {
        lastStepTime = 0
        isPaused = false
        pauseStartTime = 0
    }
}

enum class AutoPauseState {
    ACTIVE,
    PAUSED,
    RESUMED,
    NEW_SESSION_REQUIRED
}
