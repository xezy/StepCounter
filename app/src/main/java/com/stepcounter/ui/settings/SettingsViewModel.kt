package com.stepcounter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepcounter.core.stride.StrideCalculator
import com.stepcounter.data.repository.UserProfileRepository
import com.stepcounter.domain.model.Gender
import com.stepcounter.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val heightCm: Float = 170f,
    val gender: Gender = Gender.MALE,
    val dailyGoal: Int = 10000,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val strideCalculator: StrideCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        userProfileRepository.getProfile()
            .onEach { profile ->
                if (profile != null) {
                    _uiState.value = SettingsUiState(
                        heightCm = profile.heightCm,
                        gender = profile.gender,
                        dailyGoal = profile.dailyGoal,
                        isLoading = false
                    )
                } else {
                    userProfileRepository.createDefaultProfile()
                    _uiState.value = SettingsUiState(isLoading = false)
                }
            }
            .catch { e ->
                _uiState.value = SettingsUiState(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun updateSettings(heightCm: Float, gender: Gender, dailyGoal: Int) {
        viewModelScope.launch {
            val currentStride = strideCalculator.getCurrentStride()
            val profile = UserProfile(
                heightCm = heightCm,
                gender = gender,
                calibratedStride = currentStride,
                dailyGoal = dailyGoal
            )
            userProfileRepository.saveProfile(profile)
            strideCalculator.updateUserProfile(heightCm, gender)
        }
    }
}
