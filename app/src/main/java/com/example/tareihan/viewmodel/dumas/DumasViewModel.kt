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
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
            isLoading = true
            errorMessage = null

            try {
                // Validate required fields first
                if (isAnyFieldEmpty(dumas)) {
                    errorMessage = "Semua field wajib diisi!"
                    isLoading = false
                    return@launch
                }

                // Prepare individual form data fields (handle nullable values)
                val judulPart = (dumas.judul ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val isiPengaduanPart = (dumas.isi_pengaduan ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val namaPengaduPart = (dumas.nama_pengadu ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val nomorhpPengaduPart = (dumas.nomorhp_pengadu ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPengaduPart = (dumas.email_pengadu ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val verifikasiByPart = (dumas.verifikasi_by ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val verifikasiAtPart = (dumas.verifikasi_at ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val disposisiByPart = (dumas.disposisi_by ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val disposisiToPart = (dumas.disposisi_to ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val disposisiAtPart = (dumas.disposisi_at ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalAuditPart = (dumas.tanggal_audit ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val nilaiAuditPart = (dumas.nilai_audit ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val keteranganPart = (dumas.keterangan ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val createdByPart = (dumas.created_by ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

                // Prepare the file part for the multipart request
                val filePart = fileUri?.let { uri ->
                    try {
                        val file = createTempFileFromUri(uri)
                        val requestFile = file.asRequestBody(getMimeType(file.name).toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } catch (e: Exception) {
                        errorMessage = "Error processing file: ${e.message}"
                        null
                    }
                }

                // Call the API with individual form fields - sesuai dengan interface API
                val response: Response<DataResponse<Unit>> = apiService.post_dumas(
                    judul = judulPart,
                    isiPengaduan = isiPengaduanPart,
                    namaPengadu = namaPengaduPart,
                    nomorhpPengadu = nomorhpPengaduPart,
                    emailPengadu = emailPengaduPart,
                    verifikasiBy = verifikasiByPart,
                    verifikasiAt = verifikasiAtPart,
                    disposisiBy = disposisiByPart,
                    disposisiTo = disposisiToPart,
                    disposisiAt = disposisiAtPart,
                    tanggalAudit = tanggalAuditPart,
                    nilaiAudit = nilaiAuditPart,
                    keterangan = keteranganPart,
                    createdBy = createdByPart,
                    file = filePart // Nullable sesuai dengan API interface
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        getDumas() // Refresh the data after successful submission
                        isSuccess = true
                        errorMessage = null
                    } else {
                        isSuccess = false
                        errorMessage = body?.message ?: "Gagal menambahkan data"
                    }
                } else {
                    isSuccess = false
                    errorMessage = "Gagal menambahkan data: ${response.code()} - ${response.message()}"

                    // Try to get more detailed error from response body
                    response.errorBody()?.string()?.let { errorBody ->
                        errorMessage = "$errorMessage - $errorBody"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
                e.printStackTrace()
                isSuccess = false
            } finally {
                isLoading = false
            }
        }
    }

    // Helper function to validate if any required field is empty
    private fun isAnyFieldEmpty(dumas: dumas): Boolean {
        return dumas.judul.isNullOrBlank() ||
                dumas.isi_pengaduan.isNullOrBlank() ||
                dumas.nama_pengadu.isNullOrBlank() ||
                dumas.nomorhp_pengadu.isNullOrBlank() ||
                dumas.email_pengadu.isNullOrBlank() ||
                dumas.verifikasi_by.isNullOrBlank() ||
                dumas.verifikasi_at.isNullOrBlank() ||
                dumas.disposisi_by.isNullOrBlank() ||
                dumas.disposisi_to.isNullOrBlank() ||
                dumas.disposisi_at.isNullOrBlank() ||
                dumas.tanggal_audit.isNullOrBlank() ||
                dumas.nilai_audit.isNullOrBlank() ||
                dumas.keterangan.isNullOrBlank() ||
                dumas.created_by.isNullOrBlank()
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

    // Helper function to get MIME type from file extension
    private fun getMimeType(fileName: String): String {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }
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
}