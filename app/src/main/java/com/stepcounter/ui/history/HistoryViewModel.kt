package com.stepcounter.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepcounter.data.repository.StepRepository
import com.stepcounter.domain.model.DailySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val history: List<DailySummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory(7)
    }

    fun loadHistory(days: Int) {
        stepRepository.getDailyHistory(days)
            .onEach { summaries ->
                _uiState.value = HistoryUiState(
                    history = summaries,
                    isLoading = false
                )
            }
            .catch { e ->
                _uiState.value = HistoryUiState(isLoading = false)
            }
            .launchIn(viewModelScope)
    }
}
