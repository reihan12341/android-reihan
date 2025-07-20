package com.example.tareihan.viewmodel.authviewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.LoginResponse.LoginResponse
import com.example.tareihan.dto.LoginResponse.RegisterResponse
import com.example.tareihan.dto.user.User
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val apiService: ApiService) : ViewModel() {
    private val _loginState = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginState: StateFlow<Result<LoginResponse>?> = _loginState
    var isLogin by mutableStateOf(false)

    private val _registerState = MutableStateFlow<Result<RegisterResponse>?>(null)
    val registerState: StateFlow<Result<RegisterResponse>?> = _registerState

    private val _profileState = MutableStateFlow<Result<User>?>(null)
    val profileState: StateFlow<Result<User>?> = _profileState
    var user by mutableStateOf<User?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    _loginState.value = Result.success(response.body()!!)
                    isLogin = true
                } else {
                    _loginState.value = Result.failure(Exception("Login gagal: ${response.message()}"))
                }
            } catch (e: Exception) {
                _loginState.value = Result.failure(e)
            }
        }
    }

    fun register(
        name: String,
        jenis_kelamin: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.register(
                    name = name,
                    jenis_kelamin = jenis_kelamin,
                    email = email,
                    password = password
                )
                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = Result.success(response.body()!!)
                } else {
                    _registerState.value = Result.failure(Exception("Register gagal: ${response.message()}"))
                }
            } catch (e: Exception) {
                _registerState.value = Result.failure(e)
            }
        }
    }

    fun profile() {
        viewModelScope.launch {
            try {
                val response = apiService.profile()
                if (response.isSuccessful && response.body() != null) {
                    user = response.body()!!.data
                    _profileState.value = Result.success(user) as Result<User>?
                } else {
                    _profileState.value = Result.failure(Exception("Gagal mengambil profil: ${response.message()}"))
                }
            } catch (e: Exception) {
                _profileState.value = Result.failure(e)
            }
        }
    }
}
