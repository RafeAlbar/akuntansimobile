package com.example.akuntansi.ui.transaksi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akuntansi.data.model.transaksi.StoreTransaksiRequest
import com.example.akuntansi.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class TransaksiUiState(
    val loading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class TransaksiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransaksiUiState())
    val uiState: StateFlow<TransaksiUiState> = _uiState.asStateFlow()

    fun clearMessage() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun simpan(req: StoreTransaksiRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, successMessage = null, errorMessage = null) }

            runCatching {
                ApiClient.apiService.storeTransaksi(req)
            }.onSuccess { res ->
                // Ambil "ok" & "message" pakai refleksi biar aman walau DTO response beda-beda
                val ok = readBoolean(res, listOf("ok", "success", "status")) ?: true
                val msg = readString(res, listOf("message", "msg", "pesan"))

                if (ok) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            successMessage = msg ?: "Transaksi berhasil disimpan"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            errorMessage = msg ?: "Gagal menyimpan transaksi"
                        )
                    }
                }
            }.onFailure { e ->
                val msg = when (e) {
                    is HttpException -> "HTTP ${e.code()} - ${e.message()}"
                    else -> e.message ?: "Gagal menyimpan transaksi"
                }
                _uiState.update { it.copy(loading = false, errorMessage = msg) }
            }
        }
    }

    // =========================
    // Helper refleksi (tanpa kotlin-reflect)
    // =========================
    private fun readString(obj: Any, names: List<String>): String? {
        for (name in names) {
            val getter = "get" + name.replaceFirstChar { it.uppercaseChar() }

            // coba method getter
            try {
                val m = obj.javaClass.methods.firstOrNull { it.name == getter && it.parameterCount == 0 }
                val v = m?.invoke(obj)
                if (v is String) return v
            } catch (_: Throwable) {}

            // coba field langsung
            try {
                val f = obj.javaClass.declaredFields.firstOrNull { it.name == name }
                f?.isAccessible = true
                val v = f?.get(obj)
                if (v is String) return v
            } catch (_: Throwable) {}
        }
        return null
    }

    private fun readBoolean(obj: Any, names: List<String>): Boolean? {
        for (name in names) {
            val getter = "get" + name.replaceFirstChar { it.uppercaseChar() }

            // coba method getter
            try {
                val m = obj.javaClass.methods.firstOrNull { it.name == getter && it.parameterCount == 0 }
                val v = m?.invoke(obj)
                if (v is Boolean) return v
            } catch (_: Throwable) {}

            // coba field langsung
            try {
                val f = obj.javaClass.declaredFields.firstOrNull { it.name == name }
                f?.isAccessible = true
                val v = f?.get(obj)
                if (v is Boolean) return v
            } catch (_: Throwable) {}
        }
        return null
    }
}
