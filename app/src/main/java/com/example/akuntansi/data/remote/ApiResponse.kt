package com.example.akuntansi.data.remote

data class ApiResponse<T>(
    val ok: Boolean,
    val data: T? = null,
    val message: String? = null
)
