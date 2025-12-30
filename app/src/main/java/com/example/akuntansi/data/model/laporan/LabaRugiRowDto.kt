package com.example.akuntansi.data.model.laporan

data class LabaRugiRowDto(
    val kode_akun: String?,
    val nama_akun: String?,
    val kategori_akun: String?,
    val debet: Double?,
    val kredit: Double?
)
