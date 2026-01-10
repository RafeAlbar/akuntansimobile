package com.example.akuntansi.data.model.transaksi

data class StoreTransaksiResponse(
    val ok: Boolean,
    val message: String,
    val no_transaksi: String? = null
)
