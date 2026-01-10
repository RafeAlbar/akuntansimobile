package com.example.akuntansi.data.model.repository

import com.example.akuntansi.data.model.transaksi.BarangDto
import com.example.akuntansi.data.model.transaksi.InventarisStoreRequest
import com.example.akuntansi.data.model.transaksi.InventarisStoreResponse
import com.example.akuntansi.data.model.transaksi.PelangganDto
import com.example.akuntansi.data.model.transaksi.PemasokDto
import com.example.akuntansi.data.remote.ApiClient
import com.example.akuntansi.data.remote.ApiService

class InventarisRepository(
    private val api: ApiService = ApiClient.apiService
) {

    suspend fun getPemasok(): Result<List<PemasokDto>> = runCatching {
        val res = api.listPemasok()
        if (!res.ok) throw Exception(res.message ?: "Gagal memuat pemasok")

        (res.data ?: emptyList())
            .distinctBy { it.idPemasok }
            .sortedBy { it.kodePemasok ?: "" }
    }

    suspend fun getPelanggan(): Result<List<PelangganDto>> = runCatching {
        val res = api.listPelanggan()
        if (!res.ok) throw Exception(res.message ?: "Gagal memuat pelanggan")
        res.data ?: emptyList()
    }

    suspend fun getBarangByPemasok(pemasokId: Int): Result<List<BarangDto>> = runCatching {
        val res = api.getBarangByPemasok(pemasokId)
        if (!res.ok) throw Exception(res.message ?: "Gagal memuat barang pemasok")
        res.data ?: emptyList()
    }

    suspend fun getBarangSemua(): Result<List<BarangDto>> = runCatching {
        val res = api.getBarangSemua()
        if (!res.ok) throw Exception(res.message ?: "Gagal memuat barang")
        res.data ?: emptyList()
    }

    suspend fun storeInventaris(body: InventarisStoreRequest): Result<InventarisStoreResponse> = runCatching {
        val res = api.storeInventaris(body)
        if (!res.ok) throw Exception(res.message ?: "Gagal menyimpan transaksi")
        res
    }
}
