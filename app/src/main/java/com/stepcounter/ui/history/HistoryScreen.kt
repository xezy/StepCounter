package com.stepcounter.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stepcounter.domain.model.DailySummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onSessionClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDays by remember { mutableStateOf(7) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedDays = 7; viewModel.loadHistory(7) },
                    enabled = selectedDays != 7
                ) {
                    Text("7 Days")
                }
                Button(
                    onClick = { selectedDays = 30; viewModel.loadHistory(30) },
                    enabled = selectedDays != 30
                ) {
                    Text("30 Days")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(uiState.history) { summary ->
                    DailySummaryItem(
                        summary = summary,
                        onClick = { onSessionClick(0L) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryChart(history: List<DailySummary>) {
    val modelProducer = rememberChartModelProducer()

    LaunchedEffect(history) {
        modelProducer.runTransaction {
            lineChart {
                series(
                    history.mapIndexed { index, _ -> entryOf(index.toFloat(), history[index].totalSteps.toFloat()) }
                )
            }
        }
    }

    Chart(
        chart = lineChart(),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    )
}

@Composable
fun DailySummaryItem(
    summary: DailySummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = summary.date,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${summary.totalSteps} steps",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = if (summary.goalAchieved) "Goal Met!" else "${summary.sessionCount} sessions",
                style = MaterialTheme.typography.bodySmall,
                color = if (summary.goalAchieved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
