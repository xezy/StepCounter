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
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    ) {
        val strokeWidth = 16.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        drawCircle(
            color = MaterialTheme.colorScheme.surfaceVariant,
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )
        drawArc(
            color = MaterialTheme.colorScheme.primary,
            startAngle = -90f,
            sweepAngle = 360 * progress.coerceIn(0f, 1f),
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )
    }
}
