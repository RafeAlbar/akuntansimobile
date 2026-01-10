package com.example.akuntansi.data.model.transaksi



data class StoreTransaksiRequest(
    val user_id: Long = 0L,
    val tipe: String,
    val nominal: Long,
    val tanggal: String,              // format aman: "YYYY-MM-DD"
    val keterangan: String? = null,

    // Wajib kalau tipe == "Manual"
    val akun_debet_id: Int? = null,
    val akun_kredit_id: Int? = null,

    // Wajib kalau tipe == "Bayar Utang Usaha"
    val kode_pemasok: String? = null,
    val no_transaksi: String? = null,

    // Wajib kalau tipe == "Bayar Piutang Usaha"
    val id_pelanggan: Int? = null
)
