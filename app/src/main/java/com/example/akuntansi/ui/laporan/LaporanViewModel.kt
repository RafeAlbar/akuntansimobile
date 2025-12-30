package com.example.akuntansi.ui.laporan

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akuntansi.data.model.laporan.LabaRugiResponse
import com.example.akuntansi.data.model.laporan.NeracaResponse
import com.example.akuntansi.data.remote.ApiClient
import kotlinx.coroutines.launch

class LaporanViewModel : ViewModel() {

    // ✅ ini HARUS state supaya Compose recompose
    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var labaRugi by mutableStateOf<LabaRugiResponse?>(null)
        private set

    var neraca by mutableStateOf<NeracaResponse?>(null)
        private set

    fun loadAll(userId: Int = 0) {
        loading = true
        error = null

        viewModelScope.launch {
            try {
                val api = ApiClient.api // ✅ sesuai ApiClient kamu

                val lr = api.getLabaRugi(userId = userId)
                val nr = api.getNeraca(userId = userId)

                // ✅ set state -> UI auto update
                labaRugi = lr
                neraca = nr

                // ✅ debug log biar kelihatan datanya masuk
                Log.d("LAPORAN_VM", "LabaRugi ok=${lr.ok} laba=${lr.laba_bersih}")
                Log.d("LAPORAN_VM", "Neraca ok=${nr.ok} aset=${nr.data.aset.size}")

            } catch (e: Exception) {
                error = e.message ?: "Gagal memuat laporan"
                Log.e("LAPORAN_VM", "Error loadAll", e)
            } finally {
                loading = false
            }
        }
    }
}
