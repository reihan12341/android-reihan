package com.example.tareihan.ui.screens.ScheduleItem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(schedules) { schedule ->
                        ScheduleCard(schedule)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(schedule: ScheduleItem) {
    // Format dates for better readability
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
    val startDate = try {
        LocalDate.parse(schedule.start_date).format(formatter)
    } catch (e: Exception) {
        schedule.start_date // Fallback to raw string
    }
    val endDate = try {
        LocalDate.parse(schedule.end_date).format(formatter)
    } catch (e: Exception) {
        schedule.end_date // Fallback to raw string
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = schedule.opd,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { contentDescription = "Organization: ${schedule.opd}" }
            )
            Text(
                text = schedule.keterangan,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { contentDescription = "Description: ${schedule.keterangan}" }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tanggal: $startDate - $endDate",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { contentDescription = "Date: $startDate to $endDate" }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tim Auditor:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { contentDescription = "Auditor Team" }
            )
            schedule.team.forEach { member ->
                Text(
                    text = "• $member",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .semantics { contentDescription = "Team member: $member" }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScheduleScreenPreview() {
    val dummyScheduleList = listOf(
        ScheduleItem(
            opd_id = 1,
            opd = "Dinas Kesehatan",
            keterangan = "Mengawasi layanan kesehatan masyarakat",
            start_date = "2023-11-01",
            end_date = "2023-11-03",
            team = listOf("Auditor A", "Auditor B", "Auditor C"),
            team_ids = listOf(1, 2, 3)
        ),
        ScheduleItem(
            opd_id = 2,
            opd = "Dinas Pendidikan",
            keterangan = "Mengevaluasi sistem pendidikan",
            start_date = "2023-11-05",
            end_date = "2023-11-07",
            team = listOf("Auditor X", "Auditor Y"),
            team_ids = listOf(4, 5)
        )
    )

    ListScheduleScreen(
        viewModel = viewModel<ScheduleViewModel>().apply {
            // Simulate setting schedules for preview
            _schedules.value = dummyScheduleList
        }
    )
}