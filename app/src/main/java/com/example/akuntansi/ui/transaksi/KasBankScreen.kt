package com.example.akuntansi.ui.transaksi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.akuntansi.data.model.transaksi.StoreTransaksiRequest
import kotlinx.coroutines.launch

data class TipeOption(val label: String, val value: String)

private fun nominalToLong(input: String): Long {
    val digits = input.replace(Regex("[^0-9]"), "")
    return digits.toLongOrNull() ?: 0L
}

private fun tanggalToIso(input: String): String {
    val cleaned = input.replace(" ", "")
    val parts = cleaned.split("/")
    return if (parts.size == 3) {
        val dd = parts[0].padStart(2, '0')
        val mm = parts[1].padStart(2, '0')
        val yyyy = parts[2]
        "$yyyy-$mm-$dd"
    } else input
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasBankScreen(
    onBack: () -> Unit,
    vm: TransaksiViewModel = viewModel()
) {
    val primary = Color(0xFF2F2FBF)

    // form state
    var expanded by remember { mutableStateOf(false) }

    // ✅ default pilih item pertama (bukan Manual lagi)
    var selectedOption by remember {
        mutableStateOf(TipeOption(label = "Bayar Gaji", value = "Bayar Gaji"))
    }

    var tanggal by remember { mutableStateOf("11/12/2025") }
    var nominal by remember { mutableStateOf("80.000") }
    var keterangan by remember { mutableStateOf("") }

    // tambahan (conditional)
    var kodePemasok by remember { mutableStateOf("") }
    var noTransaksiRelasi by remember { mutableStateOf("") } // utk utang/piutang
    var idPelanggan by remember { mutableStateOf("") }

    val uiState by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
    }

    // ✅ MANUAL dihapus dari list
    val tipeList = remember {
        listOf(
            TipeOption("Bayar Gaji", "Bayar Gaji"),
            TipeOption("Bayar Listrik/Telepon/Internet/Air", "Bayar Listrik/Telepon/Internet/Air"),
            TipeOption("Bayar Iklan/Promosi", "Bayar Iklan/Promosi"),
            TipeOption("Bayar Transportasi (Ongkir, BBM, dll)", "Bayar Transportasi (Ongkir, BBM, dll)"),
            TipeOption("Bayar Sewa Ruko/Outlet/dll", "Bayar Sewa Ruko/Outlet/dll"),
            TipeOption("Bayar Pemeliharaan (Servis, dll)", "Bayar Pemeliharaan (Servis, dll)"),
            TipeOption("Bayar Pajak", "Bayar Pajak"),
            TipeOption("Bayar Lain-lain", "Bayar Lain-lain"),

            TipeOption("Bayar Utang Bank", "Bayar Utang Bank"),
            TipeOption("BAYAR UTANG PEMASOK", "Bayar Utang Usaha"),
            TipeOption("TERIMA PIUTANG PELANGGAN", "Bayar Piutang Usaha"),
            TipeOption("Bayar Utang Lainnya", "Bayar Utang Lainnya"),
            TipeOption("Bayar Bunga Bank", "Bayar Bunga Bank"),

            TipeOption("Beli Peralatan Tunai", "Beli Peralatan Tunai"),
            TipeOption("Beli ATK Tunai", "Beli ATK Tunai"),
            TipeOption("Beli Tanah Tunai", "Beli Tanah Tunai"),
            TipeOption("Beli Persediaan Tunai", "Beli Persediaan Tunai"),
            TipeOption("Membuat/Beli Bangunan Tunai", "Membuat/Beli Bangunan Tunai"),
            TipeOption("Beli Kendaraan Tunai", "Beli Kendaraan Tunai"),

            TipeOption("Jual Tanah", "Jual Tanah"),
            TipeOption("Jual Bangunan", "Jual Bangunan"),
            TipeOption("Jual Kendaraan", "Jual Kendaraan"),
            TipeOption("Jual Jasa", "Jual Jasa"),

            TipeOption("Pinjam Uang di Bank", "Pinjam Uang di Bank"),
            TipeOption("Pinjam Uang Lainnya", "Pinjam Uang Lainnya"),
            TipeOption("Pendapatan Bunga", "Pendapatan Bunga"),
            TipeOption("Pendapatan Lain-lain (Komisi/Hadiah)", "Pendapatan Lain-lain (Komisi/Hadiah)"),
            TipeOption("Setoran Pemilik", "Setoran Pemilik"),
            TipeOption("Pengambilan Pribadi", "Pengambilan Pribadi")
        )
    }

    val tipeBackend = selectedOption.value
    val isUtangUsaha = tipeBackend == "Bayar Utang Usaha"
    val isPiutangUsaha = tipeBackend == "Bayar Piutang Usaha"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Button(
                        enabled = !uiState.loading,
                        onClick = {
                            val tanggalIso = tanggalToIso(tanggal)
                            val nominalAngka = nominalToLong(nominal)

                            if (nominalAngka <= 0) {
                                scope.launch { snackbarHostState.showSnackbar("Nominal harus > 0") }
                                return@Button
                            }

                            if (isUtangUsaha) {
                                if (kodePemasok.isBlank() || noTransaksiRelasi.isBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar("Isi Kode Pemasok & No Transaksi") }
                                    return@Button
                                }
                            }

                            if (isPiutangUsaha) {
                                if (idPelanggan.isBlank() || noTransaksiRelasi.isBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar("Isi ID Pelanggan & No Transaksi") }
                                    return@Button
                                }
                            }

                            val req = StoreTransaksiRequest(
                                user_id = 0L, // ✅ 0 bukan null
                                tipe = tipeBackend,
                                nominal = nominalAngka,
                                tanggal = tanggalIso,
                                keterangan = keterangan.ifBlank { null },

                                // ✅ manual dihapus -> selalu null
                                akun_debet_id = null,
                                akun_kredit_id = null,

                                kode_pemasok = if (isUtangUsaha) kodePemasok else null,
                                no_transaksi = if (isUtangUsaha || isPiutangUsaha) noTransaksiRelasi else null,
                                id_pelanggan = if (isPiutangUsaha) idPelanggan.toIntOrNull() else null
                            )

                            vm.simpan(req)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Menyimpan...")
                        } else {
                            Text("Simpan", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tipe Transaksi", fontSize = 12.sp, color = Color(0xFF8A8A8A))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = selectedOption.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tipeList.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.label) },
                            onClick = {
                                selectedOption = opt
                                expanded = false

                                // reset conditional fields biar bersih
                                kodePemasok = ""
                                idPelanggan = ""
                                noTransaksiRelasi = ""
                            }
                        )
                    }
                }
            }

            Text("Tanggal", fontSize = 12.sp, color = Color(0xFF8A8A8A))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = tanggal,
                onValueChange = { tanggal = it },
                singleLine = true,
                placeholder = { Text("dd/mm/yyyy atau yyyy-mm-dd") }
            )

            Text("Nominal", fontSize = 12.sp, color = Color(0xFF8A8A8A))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "Rp. $nominal",
                onValueChange = { newValue -> nominal = newValue.replace("Rp.", "").trim() },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text("Keterangan", fontSize = 12.sp, color = Color(0xFF8A8A8A))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = keterangan,
                onValueChange = { keterangan = it },
                singleLine = true
            )

            // =====================
            // CONDITIONAL FIELDS
            // =====================

            if (isUtangUsaha) {
                Divider()
                Text("Bayar Utang Usaha", fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = kodePemasok,
                    onValueChange = { kodePemasok = it },
                    singleLine = true,
                    label = { Text("Kode Pemasok") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = noTransaksiRelasi,
                    onValueChange = { noTransaksiRelasi = it },
                    singleLine = true,
                    label = { Text("No Transaksi (Utang)") }
                )
            }

            if (isPiutangUsaha) {
                Divider()
                Text("Bayar Piutang Usaha", fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = idPelanggan,
                    onValueChange = { idPelanggan = it },
                    singleLine = true,
                    label = { Text("ID Pelanggan") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = noTransaksiRelasi,
                    onValueChange = { noTransaksiRelasi = it },
                    singleLine = true,
                    label = { Text("No Transaksi (Piutang)") }
                )
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
