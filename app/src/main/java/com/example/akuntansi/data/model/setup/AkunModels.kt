package com.example.akuntansi.data.model.setup

data class AkunDto(
    val id: Long,
    val kode_akun: String,
    val nama_akun: String
)

data class ApiResponse<T>(
    val ok: Boolean,
    val data: T
)
