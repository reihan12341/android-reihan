package com.example.tareihan.viewmodel.disposisi_irbanda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.disposisi_irbanda.disposisi_irbanda
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class Disposisi_irbandaViewModel(private val apiService: ApiService) : ViewModel() {

    var disposisiIrbandaList by mutableStateOf<List<disposisi_irbanda>>(emptyList())
    var disposisiIrbandaShow by mutableStateOf<disposisi_irbanda?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun getAllDisposisiIrbanda() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<List<disposisi_irbanda>>> = apiService.get_DisposisiIrbanda()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        disposisiIrbandaList = body.data
                    } else {
                        errorMessage = "Data kosong dari server."
                    }
                } else {
                    errorMessage = "Gagal: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengambil data."
            } finally {
                isLoading = false
            }
        }
    }

    fun getDisposisiIrbandaById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.get_DisposisiIrbanda_by_id(id)
                if (response.isSuccessful) {
                    disposisiIrbandaShow = response.body()?.data
                } else {
                    errorMessage = "Gagal memuat data: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan saat mengambil data"
            } finally {
                isLoading = false
            }
        }
    }

    fun postDisposisiIrbanda(disposisi: disposisi_irbanda) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<Unit>> = apiService.post_DisposisiIrbanda(disposisi)
                if (response.isSuccessful) {
                    getAllDisposisiIrbanda()
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

    fun updateDisposisiIrbanda(id: Int, disposisi: disposisi_irbanda) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response: Response<DataResponse<Unit>> = apiService.update_DisposisiIrbanda(id, disposisi)
                if (response.isSuccessful) {
                    getAllDisposisiIrbanda()
                } else {
                    errorMessage = "Gagal memperbarui data: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengupdate data."
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteDisposisiIrbanda(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.delete_DisposisiIrbanda(id)
                if (response.isSuccessful) {
                    getAllDisposisiIrbanda()
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
