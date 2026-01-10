package com.example.akuntansi.data.model.transaksi

import com.google.gson.annotations.SerializedName

data class InventarisStoreItemRequest(
    @SerializedName("barang_id") val barangId: Int,
    @SerializedName("qty") val qty: Double,                 // backend kamu numeric
    @SerializedName("satuan") val satuan: String? = null,
    @SerializedName("harga") val harga: Double,             // numeric
    @SerializedName("hargajual") val hargaJual: Double? = null,
    @SerializedName("subtotal") val subtotal: Double? = null,
    @SerializedName("harga_mentah") val hargaMentah: Double? = null
)

data class InventarisStoreRequest(
    @SerializedName("tipe") val tipe: String,               // "Penjualan" / "Inventaris"
    @SerializedName("tipe_pembayaran") val tipePembayaran: Int, // 1/2
    @SerializedName("tanggal") val tanggal: String,         // yyyy-mm-dd atau dd/mm/yyyy
    @SerializedName("pelanggan_id") val pelangganId: Int? = null,
    @SerializedName("party_id") val partyId: Int? = null,
    @SerializedName("no_transaksi") val noTransaksi: String? = null, // wajib kalau Inventaris
    @SerializedName("biaya_lain") val biayaLain: Double? = 0.0,
    @SerializedName("diskon_nominal") val diskonNominal: Double? = 0.0,
    @SerializedName("pajak_persen") val pajakPersen: Double? = 11.0,
    @SerializedName("apply_pajak") val applyPajak: Boolean, // backend validate boolean (true/false/1/0)
    @SerializedName("items") val items: List<InventarisStoreItemRequest>,
    @SerializedName("kode_pemasok") val kodePemasok: String? = null
)

data class InventarisStoreResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("no_transaksi") val noTransaksi: String? = null
)
