package com.example.tareihan.dto.apiresponse

import com.example.tareihan.dto.pagination_model.Pagination

data class DataResponse<T>(
    val success : Boolean,
    val message: String,
    val data: T? = null,
    val pagination: Pagination?
)
