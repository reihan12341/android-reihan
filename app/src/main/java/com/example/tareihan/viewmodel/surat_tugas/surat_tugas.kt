package com.example.tareihan.viewmodel.surat_tugas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.surat_tugas.surat_tugas
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class SuratTugasViewModel(private val apiService: ApiService) : ViewModel() {

    var suratTugasList by mutableStateOf<List<surat_tugas>>(emptyList())
    var suratTugasSelected by mutableStateOf<surat_tugas?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    val successMessage by mutableStateOf(false)

    // GET ALL
    fun getSuratTugasList() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<List<surat_tugas>>> = apiService.get_surat_tugas()
                if (response.isSuccessful) {
                    suratTugasList = response.body()?.data ?: emptyList()
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

    // GET BY ID
    fun getSuratTugasById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.get_surat_tugas_by_id(id)
                if (response.isSuccessful) {
                    suratTugasSelected = response.body()?.data
                } else {
                    errorMessage = "Gagal memuat data: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan saat mengambil data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // POST
    fun postSuratTugas(suratTugas: surat_tugas) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<Unit>> = apiService.post_surat_tugas(suratTugas)
                if (response.isSuccessful) {
                    getSuratTugasList()
                } else {
                    errorMessage = "Gagal menambahkan data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // PUT / UPDATE
    fun updateSuratTugas(id: Int, suratTugas: surat_tugas) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<Unit>> = apiService.update_surat_tugas(id, suratTugas)
                if (response.isSuccessful) {
                    getSuratTugasList()
                } else {
                    errorMessage = "Gagal memperbarui data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengupdate data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // DELETE
    fun deleteSuratTugas(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.delete_surat_tugas(id)
                if (response.isSuccessful) {
                    getSuratTugasList()
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
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
