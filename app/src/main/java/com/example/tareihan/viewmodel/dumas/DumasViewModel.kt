package com.example.tareihan.viewmodel.dumas

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihanh.dto.retrofit_interface.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DumasViewModel(
    private val apiService: ApiService,
    private val context: Context
) : ViewModel() {
    var dumas by mutableStateOf<dumas?>(null)
    var dumasList by mutableStateOf<List<dumas>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var dumasShow by mutableStateOf<dumas?>(null)
    var isSuccess by mutableStateOf(false)

    fun getDumas() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<List<dumas>>> = apiService.get_dumas()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        dumasList = body.data
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

    fun getDumas(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.get_dumas_by_id(id)
                if (response.isSuccessful) {
                    dumasShow = response.body()?.data
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

    fun postDumas(dumas: dumas, fileUri: Uri?) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Prepare the dumas data as a JSON request body using Gson
                val dumasJson = Gson().toJson(dumas)
                val dumasRequestBody = dumasJson.toRequestBody("application/json".toMediaTypeOrNull())

                // Prepare the file part for the multipart request
                val filePart = fileUri?.let { uri ->
                    // Create a temporary file from the Uri
                    val file = createTempFileFromUri(uri)
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                }

                // Call the API with the dumas data and optional file
                val response: Response<DataResponse<Unit>> = apiService.post_dumas(
                    dumas = dumasRequestBody,
                    file = filePart
                )

                if (response.isSuccessful) {
                    getDumas() // Refresh the data after successful submission
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

    // Helper function to create a temporary file from a Uri
    private fun createTempFileFromUri(uri: Uri): File {
        val tempFile = File.createTempFile("upload_", ".tmp", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    fun putDumas(id: Int, dumas: dumas) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.update_dumas(id, dumas)
                if (response.isSuccessful) {
                    getDumas()
                    isSuccess = true
                } else {
                    isSuccess = false
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

    fun deleteDumas(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = apiService.delete_dumas(id)
                if (response.isSuccessful) {
                    getDumas()
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