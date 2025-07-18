package com.example.tareihan.dto.pagination_model

data class Pagination(
    val current_page: Int,
    val per_page: Int,
    val total_pages: Int,
    val total_items: Int
)
