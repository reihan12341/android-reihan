package com.example.tareihan.viewmodel.dumas

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihan.dto.dumas.update_audit_disposisi
import com.example.tareihan.dto.dumas.update_disposisi_request
import com.example.tareihan.dto.dumas.update_status_request
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException

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
                    if (body != null && body.success && body.data != null) {
                        dumasList = body.data
                    } else {
                        errorMessage = body?.message ?: "Data kosong dari server."
                    }
                } else {
                    errorMessage = "Gagal: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
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
            errorMessage = null

            try {
                val response: Response<DataResponse<dumas>> = apiService.get_dumas_by_id(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        dumasShow = body.data
                    } else {
                        errorMessage = body?.message ?: "Data tidak ditemukan"
                    }
                } else {
                    errorMessage = "Gagal memuat data: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan saat mengambil data"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun postDumas(dumas: dumas, fileUri: Uri?) {
        viewModelScope.launch {
            try {
                // Prepare RequestBody for each field
                val judul = dumas.judul.toRequestBody("text/plain".toMediaTypeOrNull())
                val isiPengaduan = dumas.isi_pengaduan.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaPengadu = dumas.nama_pengadu.toRequestBody("text/plain".toMediaTypeOrNull())
                val nomorhpPengadu = dumas.nomorhp_pengadu.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPengadu = dumas.email_pengadu.toRequestBody("text/plain".toMediaTypeOrNull())
                val keterangan = dumas.keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val createdBy = dumas.created_by.toRequestBody("text/plain".toMediaTypeOrNull())

                // Prepare file part if available
                val filePart: MultipartBody.Part? = fileUri?.let { uri ->
                    createFilePart(uri)
                }

                // Make API call
                val response = apiService.post_dumas(
                    judul = judul,
                    isiPengaduan = isiPengaduan,
                    namaPengadu = namaPengadu,
                    nomorhpPengadu = nomorhpPengadu,
                    emailPengadu = emailPengadu,
                    keterangan = keterangan,
                    createdBy = createdBy,
                    file = filePart
                )

                if (response.isSuccessful) {
                    isSuccess = true
                    errorMessage = null
                } else {
                    isSuccess = false
                    errorMessage = "Failed to submit dumas: ${response.message()}"
                }
            } catch (e: Exception) {
                isSuccess = false
                errorMessage = "Error: ${e.message}"
            }
        }
    }
    // Helper function to create a temporary file from a Uri
    private fun createFilePart(uri: Uri): MultipartBody.Part? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val fileBytes = inputStream.readBytes()
                val fileName = getFileName(uri)

                val fileRequestBody = fileBytes.toRequestBody("*/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", fileName, fileRequestBody)
            }
        } catch (e: IOException) {
            errorMessage = "Failed to read file: ${e.message}"
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else {
                "file_${System.currentTimeMillis()}"
            }
        } ?: "file_${System.currentTimeMillis()}"
    }

    fun putDumas(id: Int, dumas: dumas) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response: Response<DataResponse<Unit>> = apiService.update_dumas(id, dumas)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas()
                        isSuccess = true
                    } else {
                        isSuccess = false
                        errorMessage = body?.message ?: "Gagal memperbarui data"
                    }
                } else {
                    isSuccess = false
                    errorMessage = "Gagal memperbarui data: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat mengupdate data."
                e.printStackTrace()
                isSuccess = false
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
                val response: Response<DataResponse<Unit>> = apiService.delete_dumas(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas()
                    } else {
                        errorMessage = body?.message ?: "Gagal menghapus data"
                    }
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan saat menghapus data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Function to reset success state
    fun resetSuccessState() {
        isSuccess = false
        errorMessage = null
    }

    fun update_status_dumas(id:Int,status:String){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val updateStatusRequest = update_status_request(
                    status = status,
                )
                val response: Response<DataResponse<Unit>> = apiService.update_status_dumas(id,updateStatusRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas()
                    } else {
                        errorMessage = body?.message ?: "Gagal menghapus data"
                    }
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            }catch (e:Exception){
                errorMessage = "Terjadi kesalahan saat menghapus data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun update_disposisi(id: Int, updateDisposisiRequest: update_disposisi_request){
        viewModelScope.launch {
            try {
                val response: Response<DataResponse<Unit>> = apiService.update_disposisi(id,updateDisposisiRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas()
                    } else {
                        errorMessage = body?.message ?: "Gagal menghapus data"
                    }
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            }catch (e:Exception){
                errorMessage = "Terjadi kesalahan saat menghapus data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun update_disposisi_audit(id: Int, updateAuditDisposisi: update_audit_disposisi){
        viewModelScope.launch {
            try {
                val response: Response<DataResponse<Unit>> = apiService.update_disposisi_audit(id,updateAuditDisposisi)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas()
                    } else {
                        errorMessage = body?.message ?: "Gagal menghapus data"
                    }
                } else {
                    errorMessage = "Gagal menghapus data: ${response.code()} - ${response.message()}"
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            }catch (e:Exception){
                errorMessage = "Terjadi kesalahan saat menghapus data."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}