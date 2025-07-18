package com.example.tareihan.dto.dumas

data class dumas(
    val id: Int? = null,
    val judul: String? = null,
    val isi_pengaduan: String? = null,
    val nama_pengadu: String? = null,
    val nomorhp_pengadu: String? = null,
    val email_pengadu: String? = null,
    val verifikasi_by: String? = null,
    val verifikasi_at: String? = null,
    val disposisi_at: String? = null,
    val disposisi_by: String? = null,
    val disposisi_to: String? = null,
    val tanggal_audit: String? = null,
    val keterangan: String? = null,
    val nilai_audit: String? = null,
    val created_by: String? = null,
    val status: String? = null,
    val attachments: List<String> = emptyList(),
)
