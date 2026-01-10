package com.example.akuntansi.data.transaksi

import com.example.akuntansi.data.model.transaksi.StoreTransaksiRequest
import com.example.akuntansi.data.model.transaksi.StoreTransaksiResponse
import com.example.akuntansi.data.remote.ApiClient

class TransaksiRepository {
    suspend fun storeTransaksi(req: StoreTransaksiRequest): StoreTransaksiResponse {
        return ApiClient.apiService.storeTransaksi(req)
    }
}
