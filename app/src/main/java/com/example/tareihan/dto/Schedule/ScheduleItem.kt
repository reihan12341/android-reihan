package com.example.tareihan.dto.Schedule

data class ScheduleItem(
    val opd_id: Int,
    val opd: String,
    val keterangan: String,
    val start_date: String,
    val end_date: String,
    val team: List<String>,
    val team_ids: List<Int>
)