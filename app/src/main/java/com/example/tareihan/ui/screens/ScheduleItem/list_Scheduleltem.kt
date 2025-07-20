package com.example.tareihan.ui.screens.ScheduleItem

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tareihan.dto.Schedule.ScheduleItem
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ListScheduleScreen(viewModel: ScheduleViewModel = koinViewModel()) {
    val schedules = viewModel.schedules.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            schedules.isEmpty() -> {
                Text(
                    text = "No schedules available",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {
                ScheduleTable(schedules = schedules)
            }
        }
    }
}

@Composable
fun ScheduleTable(schedules: List<ScheduleItem>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .horizontalScroll(scrollState)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                .padding(8.dp)
        ) {
            Text(
                text = "OPD",
                modifier = Modifier
                    .width(150.dp)
                    .padding(8.dp)
                    .semantics { contentDescription = "OPD Header" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Keterangan",
                modifier = Modifier
                    .width(200.dp)
                    .padding(8.dp)
                    .semantics { contentDescription = "Description Header" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Tanggal",
                modifier = Modifier
                    .width(200.dp)
                    .padding(8.dp)
                    .semantics { contentDescription = "Date Header" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Tim Auditor",
                modifier = Modifier
                    .width(150.dp)
                    .padding(8.dp)
                    .semantics { contentDescription = "Auditor Team Header" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Table Content
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(schedules) { schedule ->
                ScheduleTableRow(schedule)
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun ScheduleTableRow(schedule: ScheduleItem) {
    // Format dates for better readability
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    val startDate = try {
        LocalDate.parse(schedule.start_date).format(formatter)
    } catch (e: Exception) {
        schedule.start_date
    }
    val endDate = try {
        LocalDate.parse(schedule.end_date).format(formatter)
    } catch (e: Exception) {
        schedule.end_date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .semantics { contentDescription = "Schedule Row: ${schedule.opd}" }
    ) {
        Text(
            text = schedule.opd,
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp)
                .semantics { contentDescription = "Organization: ${schedule.opd}" },
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = schedule.keterangan,
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp)
                .semantics { contentDescription = "Description: ${schedule.keterangan}" },
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "$startDate - $endDate",
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp)
                .semantics { contentDescription = "Date: $startDate to $endDate" },
            style = MaterialTheme.typography.bodyMedium
        )
        Column(
            modifier = Modifier
                .width(150.dp)
                .padding(8.dp)
        ) {
            schedule.team.forEach { member ->
                Text(
                    text = "• $member",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.semantics { contentDescription = "Team member: $member" }
                )
            }
        }
    }
}