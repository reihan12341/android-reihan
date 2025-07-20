package com.example.tareihan.viewmodel.auditor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.auditor.Auditor
import com.example.tareihan.dto.auditor.auditor_response
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class AuditorViewModel(private val apiService: ApiService): ViewModel() {
    var auditor by mutableStateOf<Auditor?>(null)
    var auditorList by mutableStateOf<List<auditor_response>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isUpdateSuccess by mutableStateOf(false)
    var auditorShow by mutableStateOf<Auditor?>(null)
    fun getAuditor() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<List<auditor_response>>> = apiService.get_auditor()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        auditorList = body.data
                    } else {
                        errorMessage = "Data kosong dari server."
                    }
                } else {
                    errorMessage = "Gagal: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengambil data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun getAuditorById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.get_auditor_by_id(id)
                if (response.isSuccessful) {
                    auditorShow = response.body()?.data
                } else {
                    errorMessage = "Gagal memuat auditor: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan saat mengambil data auditor"
            } finally {
                isLoading = false
            }
        }
    }

    fun postAuditor(auditor: Auditor) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.post_auditor(auditor)
                if (response.isSuccessful) {
                    getAuditor()
                } else {
                    errorMessage = "Gagal menambahkan data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
            } finally {
                isLoading = false
            }
        }
    }

    fun putAuditor(id: Int, auditor: Auditor) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isUpdateSuccess = false

            try {
                val response = apiService.update_auditor(id, auditor)
                if (response.isSuccessful) {
                    getAuditor() // Refresh data
                    isUpdateSuccess = true
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Tidak diketahui"
                    errorMessage = "Gagal memperbarui: ${response.code()} - ${response.message()} - $errorBody"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan tak terduga."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteAuditor(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = apiService.delete_auditor(id)
                if (response.isSuccessful) {
                    getAuditor()
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menghapus data."
            } finally {
                isLoading = false
            }
        }
    }
}
