package com.stepcounter.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Step Counter") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Text("History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Text("Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CircularStepIndicator(
                steps = uiState.todaySteps,
                goal = uiState.dailyGoal
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${uiState.todaySteps} steps",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Distance: ${String.format("%.2f", uiState.distance)} km",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (uiState.isTracking) {
                        viewModel.stopTracking()
                    } else {
                        viewModel.startTracking()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isTracking) "Stop Tracking" else "Start Tracking")
            }
        }
    }
}

@Composable
fun CircularStepIndicator(steps: Int, goal: Int) {
    val progress = if (goal > 0) steps.toFloat() / goal else 0f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 16.dp
        )
        Text(
            text = "$steps",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
