package com.example.tareihan.viewmodel.temuan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.temuan.temuan
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class TemuanViewModel(private val apiService: ApiService) : ViewModel() {
    var isUpdateSuccess by mutableStateOf(false)
    var temuan by mutableStateOf<temuan?>(null)
    var temuanList by mutableStateOf<List<temuan>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var temuanShow by mutableStateOf<temuan?>(null)
    var isDeleteSuccess by mutableStateOf(false)

    fun getTemuan() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<List<temuan>>> = apiService.get_temuan()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        temuanList = body.data
                    } else {
                        errorMessage = "Data kosong dari server."
                    }
                } else {
                    errorMessage =
                        "Gagal: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengambil data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun postTemuan(temuan: temuan) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.post_temuan(temuan)
                if (response.isSuccessful) {
                    getTemuan()
                } else {
                    errorMessage =
                        "Gagal menambahkan data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun getTemuanById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.get_temuan_by_id(id)
                if (response.isSuccessful) {
                    temuanShow = response.body()?.data
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

    fun putTemuan(id: Int, temuan: temuan) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.update_temuan(id, temuan)
                if (response.isSuccessful) {
                    getTemuan() // Refresh data jika berhasil
                    isUpdateSuccess = true
                } else {
                    errorMessage =
                        "Gagal memperbarui data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengupdate data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteTemuan(id: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = apiService.delete_temuan(id)
                if (response.isSuccessful) {
                    getTemuan()
                    isDeleteSuccess = true
                } else {
                    errorMessage =
                        "Gagal menghapus data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menghapus data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

}
