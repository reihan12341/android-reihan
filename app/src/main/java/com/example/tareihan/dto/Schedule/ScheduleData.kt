package com.example.tareihan.dto.Schedule

data class ScheduleData(
    val schedule: List<ScheduleItem>,
    val fitness: Int,
    val total_opd: Int,
    val total_auditors: Int
)

