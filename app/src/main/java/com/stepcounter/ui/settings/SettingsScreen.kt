package com.stepcounter.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stepcounter.domain.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var height by remember { mutableStateOf(uiState.heightCm.toString()) }
    var gender by remember { mutableStateOf(uiState.gender) }
    var goal by remember { mutableStateOf(uiState.dailyGoal) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Gender", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterChip(
                    selected = gender == Gender.MALE,
                    onClick = { gender = Gender.MALE },
                    label = { Text("Male") }
                )
                FilterChip(
                    selected = gender == Gender.FEMALE,
                    onClick = { gender = Gender.FEMALE },
                    label = { Text("Female") }
                )
            }

            Column {
                Text(
                    "Daily Goal: $goal steps",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = goal.toFloat(),
                    onValueChange = { goal = it.toInt() },
                    valueRange = 1000f..30000f,
                    steps = 29,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    viewModel.updateSettings(
                        heightCm = height.toFloatOrNull() ?: 170f,
                        gender = gender,
                        dailyGoal = goal
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
        }
    }
}
