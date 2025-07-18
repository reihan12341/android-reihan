package com.example.tareihan.dto.LoginResponse

import com.example.tareihan.dto.user.User

data class LoginResponse(
    val status: Boolean,
    val success: Boolean,
    val message: String,
    val token: String,
    val user: User
)