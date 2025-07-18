package com.example.tareihan.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.user.User
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class userViewModel(private val apiService: ApiService) : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var userList by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var userShow by mutableStateOf<User?>(null)

    fun getuser() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<List<User>>> = apiService.get_user()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        userList = body.data
                    } else {
                        errorMessage = "Data kosong dari server."
                    }
                } else {
                    errorMessage = "Gagal: ${response.code()} - ${response.message()} - ${
                        response.errorBody()?.string()
                    }"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengambil data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun postUser(user: User) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.post_user(user)
                if (response.isSuccessful) {
                    getuser()
                } else {
                    errorMessage =
                        "Gagal menambahkan data: ${response.code()} - ${response.message()} - ${
                            response.errorBody()?.string()
                        }"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun getUserById(id:String){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.get_user_by_id(id)
                if (response.isSuccessful) {
                    user = response.body()?.data
                } else {
                    errorMessage = "Gagal mengambil detail: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Kesalahan saat mengambil detail."
            } finally {
                isLoading = false
            }
        }
    }

    fun putuser(id: Int, user: User) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.update_user(id, user)
                if (response.isSuccessful) {
                    getuser() // Refresh data jika berhasil
                } else {
                    errorMessage =
                        "Gagal memperbarui data: ${response.code()} - ${response.message()} - ${
                            response.errorBody()?.string()
                        }"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengupdate data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUserKerja(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = apiService.delete_user(id)
                if (response.isSuccessful) {
                    getuser()
                } else {
                    errorMessage =
                        "Gagal menghapus data: ${response.code()} - ${response.message()} - ${
                            response.errorBody()?.string()
                        }"
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

