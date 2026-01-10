package com.example.akuntansi.data.remote

import com.example.akuntansi.data.model.setup.AkunDto
import com.example.akuntansi.data.model.setup.ApiResponse
import com.example.akuntansi.data.model.setup.SaldoAwalRequest
import com.example.akuntansi.data.model.setup.SaldoAwalResponse
import com.example.akuntansi.data.model.laporan.LabaRugiResponse
import com.example.akuntansi.data.model.laporan.NeracaResponse
import com.example.akuntansi.data.model.transaksi.StoreTransaksiRequest
import com.example.akuntansi.data.model.transaksi.StoreTransaksiResponse

// ✅ inventaris DTO
import com.example.akuntansi.data.model.transaksi.ApiListResponse
import com.example.akuntansi.data.model.transaksi.PemasokDto
import com.example.akuntansi.data.model.transaksi.PelangganDto
import com.example.akuntansi.data.model.transaksi.BarangDto
import com.example.akuntansi.data.model.transaksi.InventarisStoreRequest
import com.example.akuntansi.data.model.transaksi.InventarisStoreResponse

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // =========================
    // SETUP / LAPORAN (punya kamu)
    // =========================
    @GET("mst-akun")
    suspend fun listMstAkun(): ApiResponse<List<AkunDto>>

    @GET("laporan/laba-rugi")
    suspend fun getLabaRugi(
        @Query("user_id") userId: Int = 0,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("search") search: String = ""
    ): LabaRugiResponse

    @GET("laporan/neraca")
    suspend fun getNeraca(
        @Query("user_id") userId: Int = 0,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("search") search: String = ""
    ): NeracaResponse

    @POST("saldo-awal")
    suspend fun storeSaldoAwal(@Body body: SaldoAwalRequest): SaldoAwalResponse

    // ✅ Kas & Bank (biarkan)
    @POST("buku_besar/storetransaksi")
    suspend fun storeTransaksi(@Body body: StoreTransaksiRequest): StoreTransaksiResponse

    // =========================
    // ✅ INVENTARIS / PEMBELIAN / PENJUALAN
    // (endpoint kamu balikin {ok,data,message?} → pakai ApiListResponse)
    // =========================

    @GET("buku_besar/list_pemasok")
    suspend fun listPemasok(): ApiListResponse<List<PemasokDto>>

    @GET("buku_besar/list_pelanggan")
    suspend fun listPelanggan(): ApiListResponse<List<PelangganDto>>

    @GET("barang-by-pemasok")
    suspend fun getBarangByPemasok(
        @Query("pemasok_id") pemasokId: Int
    ): ApiListResponse<List<BarangDto>>

    @GET("barang-semua")
    suspend fun getBarangSemua(): ApiListResponse<List<BarangDto>>


    @POST("inventaris/store") // ⚠️ sesuaikan dengan route API kamu yang bener
    suspend fun storeInventaris(
        @Body body: InventarisStoreRequest
    ): InventarisStoreResponse
}
