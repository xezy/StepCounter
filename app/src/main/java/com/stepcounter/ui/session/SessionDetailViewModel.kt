package com.stepcounter.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepcounter.data.repository.StepRepository
import com.stepcounter.domain.model.WalkSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val stepRepository: StepRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _session = MutableStateFlow<WalkSession?>(null)
    val session: StateFlow<WalkSession?> = _session.asStateFlow()

    init {
        val sessionId = savedStateHandle.get<Long>("sessionId") ?: 0L
        loadSession(sessionId)
    }

    private fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            stepRepository.getRecentSessions(100)
                .collect { sessions ->
                    _session.value = sessions.find { it.id == sessionId }
                }
        }
    }
}
