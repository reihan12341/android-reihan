package com.example.tareihan.viewmodel.unit_kerja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.unit_kerja.unit_kerja
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UnitKerjaViewModel(private val apiService: ApiService) : ViewModel() {

    private val _unitKerja = MutableStateFlow<unit_kerja?>(null)
    val unitKerja: StateFlow<unit_kerja?> = _unitKerja.asStateFlow()

    private val _unitKerjaList = MutableStateFlow<List<unit_kerja>>(emptyList())
    val unitKerjaList: StateFlow<List<unit_kerja>> = _unitKerjaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun getUnitKerja() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                val response: Response<DataResponse<List<unit_kerja>>> = apiService.get_unit_kerja()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.data != null) {
                        _unitKerjaList.value = body.data
                    } else {
                        _errorMessage.value = "Data kosong dari server."
                    }
                } else {
                    _errorMessage.value = "Gagal: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan saat mengambil data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUnitKerjaById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.get_unit_kerja_by_id(id)
                if (response.isSuccessful) {
                    _unitKerja.value = response.body()?.data
                } else {
                    _errorMessage.value = "Gagal mengambil detail: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Kesalahan saat mengambil detail."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postUnitKerja(unitKerja: unit_kerja) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                val response = apiService.post_unit_kerja(unitKerja)
                if (response.isSuccessful) {
                    getUnitKerja()
                    _updateSuccess.value = true
                } else {
                    _errorMessage.value = "Gagal menambahkan data: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan saat menambahkan data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun putUnitKerja(id: String, unitKerja: unit_kerja) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                val response = apiService.update_unit_kerja(id, unitKerja)
                if (response.isSuccessful) {
                    _updateSuccess.value = true
                    // Update local state with new data
                    _unitKerja.value = unitKerja
                } else {
                    _errorMessage.value = "Gagal memperbarui data: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Kesalahan saat memperbarui data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUnitKerja(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.delete_unit_kerja(id)
                if (response.isSuccessful) {
                    getUnitKerja()
                } else {
                    _errorMessage.value = "Gagal menghapus data: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Kesalahan saat menghapus data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset states
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}