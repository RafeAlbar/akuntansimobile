package com.example.akuntansi.ui.transaksi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.akuntansi.data.model.repository.InventarisRepository
import com.example.akuntansi.data.model.transaksi.BarangDto
import com.example.akuntansi.data.model.transaksi.InventarisStoreRequest
import com.example.akuntansi.data.model.transaksi.PelangganDto
import com.example.akuntansi.data.model.transaksi.PemasokDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class InventarisUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    val pemasok: List<PemasokDto> = emptyList(),
    val pelanggan: List<PelangganDto> = emptyList(),
    val barang: List<BarangDto> = emptyList()
)

class InventarisViewModel(
    private val repo: InventarisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventarisUiState())
    val uiState: StateFlow<InventarisUiState> = _uiState

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun loadParties(isPembelian: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

            if (isPembelian) {
                repo.getPemasok()
                    .onSuccess { list ->
                        _uiState.value = _uiState.value.copy(loading = false, pemasok = list)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
                    }
            } else {
                repo.getPelanggan()
                    .onSuccess { list ->
                        _uiState.value = _uiState.value.copy(loading = false, pelanggan = list)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
                    }
            }
        }
    }

    fun loadBarang(isPembelian: Boolean, pemasokId: Int?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

            if (isPembelian) {
                if (pemasokId == null) {
                    _uiState.value = _uiState.value.copy(loading = false, barang = emptyList())
                    return@launch
                }

                repo.getBarangByPemasok(pemasokId)
                    .onSuccess { list ->
                        _uiState.value = _uiState.value.copy(loading = false, barang = list)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
                    }
            } else {
                repo.getBarangSemua()
                    .onSuccess { list ->
                        _uiState.value = _uiState.value.copy(loading = false, barang = list)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
                    }
            }
        }
    }

    fun submitInventaris(req: InventarisStoreRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, successMessage = null)

            repo.storeInventaris(req)
                .onSuccess { res ->
                    val msg = buildString {
                        append(res.message ?: "Tersimpan")
                        if (!res.noTransaksi.isNullOrBlank()) append(" (${res.noTransaksi})")
                    }
                    _uiState.value = _uiState.value.copy(loading = false, successMessage = msg)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(loading = false, errorMessage = e.message)
                }
        }
    }
}

class InventarisViewModelFactory(
    private val repo: InventarisRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventarisViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
