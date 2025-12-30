package com.example.akuntansi.ui.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.akuntansi.data.model.setup.AkunDto
import com.example.akuntansi.data.model.setup.SaldoAwalRequest
import com.example.akuntansi.data.remote.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SetupViewModel : ViewModel() {

    var akunItems by mutableStateOf<List<AkunDto>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var saving by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadAkun() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                val res = ApiClient.api.listMstAkun()
                if (res.ok) {
                    akunItems = res.data
                } else {
                    error = "API mengembalikan ok=false"
                }
            } catch (e: Exception) {
                error = e.message ?: "Gagal memuat akun"
            } finally {
                loading = false
            }
        }
    }

    /**
     * Kirim saldo awal ke backend.
     * userId sementara hardcode (kalau belum login/token).
     */
    fun storeSaldoAwal(
        userId: Long,
        akunId: Long,
        tanggalUi: String,
        nominalUi: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            saving = true
            try {
                // "80.000" / "Rp. 80.000" -> "80000"
                val nominalClean = nominalUi.replace(Regex("[^0-9]"), "")
                if (nominalClean.isBlank() || nominalClean.toLong() <= 0) {
                    onError("Nominal tidak valid")
                    saving = false
                    return@launch
                }

                // "11 / 12 / 2025" -> "2025-12-11"
                val tanggalIso = toIsoDate(tanggalUi)

                val req = SaldoAwalRequest(
                    mstAkunId = akunId,
                    subAkunId = emptyList(),
                    nominal = listOf(nominalClean),
                    tanggal = listOf(tanggalIso),
                    userId = 0L
                )

                val res = ApiClient.api.storeSaldoAwal(req)

                if (res.ok) onSuccess(res.message)
                else onError(res.error ?: res.message)

            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan saldo awal")
            } finally {
                saving = false
            }
        }
    }

    // helper convert tanggal UI -> yyyy-MM-dd
    private fun toIsoDate(input: String): String {
        val cleaned = input.trim()

        // coba beberapa format input yang umum dari UI
        val candidates = listOf("dd / MM / yyyy", "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd")

        for (fmt in candidates) {
            try {
                val parser = SimpleDateFormat(fmt, Locale.getDefault())
                parser.isLenient = false
                val date = parser.parse(cleaned) ?: continue
                val out = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                return out.format(date)
            } catch (_: Exception) {

            }
        }

        // fallback kalau gagal parse
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
    }
}
