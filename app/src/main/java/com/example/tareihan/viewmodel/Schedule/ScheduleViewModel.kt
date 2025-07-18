package com.example.tareihan.ui.screens.ScheduleItem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.Schedule.ScheduleItem
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ScheduleViewModel(private val apiService: ApiService) : ViewModel() {
    internal val _schedules = MutableStateFlow<List<ScheduleItem>>(emptyList())
    val schedules: StateFlow<List<ScheduleItem>> = _schedules.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchSchedules()
    }

    fun fetchSchedules() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getSchedules()
                Log.d("ScheduleViewModel", "Response: $response")
                if (response.success) {
                    _schedules.value = response.data?.schedule ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = response.message
                    Log.e("ScheduleViewModel", "API error: ${response.message}")
                }
            } catch (e: HttpException) {
                _error.value = "HTTP error: ${e.message()}"
                Log.e("ScheduleViewModel", "HttpException: $e")
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
                Log.e("ScheduleViewModel", "IOException: $e")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("ScheduleViewModel", "Exception: $e")
            } finally {
                _isLoading.value = false
            }
        }
    }
}