package com.stepcounter.ui.dashboard

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepcounter.data.repository.StepRepository
import com.stepcounter.data.repository.UserProfileRepository
import com.stepcounter.domain.model.UserProfile
import com.stepcounter.service.StepTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val todaySteps: Int = 0,
    val dailyGoal: Int = 10000,
    val distance: Float = 0f,
    val isTracking: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val stepRepository: StepRepository,
    private val userProfileRepository: UserProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            stepRepository.getTodaySteps(),
            userProfileRepository.getProfile()
        ) { steps, profile ->
            val userProfile = profile ?: UserProfile.default()
            val distance = steps * userProfile.calibratedStride / 1000
            DashboardUiState(
                todaySteps = steps,
                dailyGoal = userProfile.dailyGoal,
                distance = distance,
                isLoading = false
            )
        }.catch { e ->
            _uiState.value = DashboardUiState(isLoading = false)
        }.launchIn(viewModelScope)
    }

    fun startTracking() {
        val intent = Intent(context, StepTrackingService::class.java).apply {
            action = StepTrackingService.ACTION_START
        }
        context.startForegroundService(intent)
        _uiState.value = _uiState.value.copy(isTracking = true)
    }

    fun stopTracking() {
        val intent = Intent(context, StepTrackingService::class.java).apply {
            action = StepTrackingService.ACTION_STOP
        }
        context.startForegroundService(intent)
        _uiState.value = _uiState.value.copy(isTracking = false)
    }
}
