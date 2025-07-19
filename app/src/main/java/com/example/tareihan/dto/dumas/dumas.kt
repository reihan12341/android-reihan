package com.example.tareihan.dto.dumas

import java.io.File

data class dumas(
    val id: Int = 0,
    val judul: String = "",
    val isi_pengaduan: String = "",
    val nama_pengadu: String = "",
    val nomorhp_pengadu: String = "",
    val email_pengadu: String = "",
    val verifikasi_by: String = "",
    val verifikasi_at: String = "",
    val disposisi_by: String = "",
    val disposisi_to: String = "",
    val disposisi_at: String = "",
    val tanggal_audit: String = "",
    val nilai_audit: String = "",
    val keterangan: String = "",
    val created_by: String = "",
    val file : File? = null,
    val filePath: String? = null
)