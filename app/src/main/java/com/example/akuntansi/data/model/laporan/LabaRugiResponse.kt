package com.example.akuntansi.data.model.laporan

data class LabaRugiResponse(
    val ok: Boolean,
    val data: List<LabaRugiRowDto> = emptyList(),
    val total_penjualan: Double? = 0.0,
    val total_hpp: Double? = 0.0,
    val total_pend_lain: Double? = 0.0,
    val total_pendapatan: Double? = 0.0,
    val total_beban: Double? = 0.0,
    val laba_bersih: Double? = 0.0
)
