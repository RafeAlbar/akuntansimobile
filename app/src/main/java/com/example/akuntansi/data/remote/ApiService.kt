package com.example.akuntansi.data.remote

import com.example.akuntansi.data.model.setup.AkunDto
import com.example.akuntansi.data.model.setup.ApiResponse
import com.example.akuntansi.data.model.setup.SaldoAwalRequest
import com.example.akuntansi.data.model.setup.SaldoAwalResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import com.example.akuntansi.data.model.laporan.LabaRugiResponse
import com.example.akuntansi.data.model.laporan.NeracaResponse
import retrofit2.http.Query

interface ApiService {

    // GET list akun
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

    // POST simpan saldo awal
    @POST("saldo-awal") // ini harus sama dengan route Laravel: /api/saldo-awal
    suspend fun storeSaldoAwal(@Body body: SaldoAwalRequest): SaldoAwalResponse


}
