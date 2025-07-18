package com.example.tareihan.dto.surat_tugas

data class surat_tugas (
    val id: Int? = null,
    val laporan_dumas_id: String? = null,
    val nomor_surat: String? = null,
    val tanggal_terbit: String? = null,
    val jenis: String? = null,
    val perihal: String? = null,
    val tujuan : String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)