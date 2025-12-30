package com.example.akuntansi.data.model.setup

import com.google.gson.annotations.SerializedName

data class SaldoAwalRequest(
    @SerializedName("mst_akun_id") val mstAkunId: Long,
    @SerializedName("sub_akun_id") val subAkunId: List<Long> = emptyList(),
    val nominal: List<String>,           // kirim string juga boleh ("80000")
    val tanggal: List<String>,           // "2025-12-11"
    @SerializedName("user_id") val userId: Long // sementara (kalau belum token)
)

data class SaldoAwalResponse(
    val ok: Boolean,
    val message: String,
    val total: Int? = null,
    val error: String? = null
)