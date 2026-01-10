package com.example.akuntansi.data.model.transaksi

import com.google.gson.annotations.SerializedName

// Response list: { ok, data, message? }
data class ApiListResponse<T>(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") val message: String? = null
)

data class PemasokDto(
    @SerializedName("id_pemasok") val idPemasok: Int,
    @SerializedName("kode_pemasok") val kodePemasok: String? = null,
    @SerializedName("nama_pemasok") val namaPemasok: String,
    @SerializedName("nama_barang") val namaBarang: String? = null
)

data class PelangganDto(
    @SerializedName("id_pelanggan") val idPelanggan: Int,
    @SerializedName("nama_pelanggan") val namaPelanggan: String
)

data class BarangDto(
    @SerializedName("id_barang") val idBarang: Int,
    @SerializedName("nama_barang") val namaBarang: String,
    @SerializedName("satuan_ukur") val satuanUkur: String? = null,
    @SerializedName("harga_satuan") val hargaSatuan: Long? = null,
    @SerializedName("harga_jual") val hargaJual: Long? = null,
    @SerializedName("kode_pemasok") val kodePemasok: String? = null
)
