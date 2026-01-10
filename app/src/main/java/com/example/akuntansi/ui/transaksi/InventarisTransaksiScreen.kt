package com.example.akuntansi.ui.transaksi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.akuntansi.data.model.repository.InventarisRepository
import com.example.akuntansi.data.model.transaksi.BarangDto
import com.example.akuntansi.data.model.transaksi.InventarisStoreItemRequest
import com.example.akuntansi.data.model.transaksi.InventarisStoreRequest
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

private data class ItemForm(
    val barangId: Int? = null,
    val qty: String = "1",
    val satuan: String = "",
    val hargaBeli: Long = 0L,
    val hargaJual: String = "0"
)

private fun digitsToLong(input: String): Long {
    val digits = input.replace(Regex("[^0-9]"), "")
    return digits.toLongOrNull() ?: 0L
}

private fun digitsToDouble(input: String): Double {
    // support 1.5 / 1,5
    val cleaned = input.trim().replace(",", ".")
    return cleaned.toDoubleOrNull() ?: 0.0
}

private fun formatRupiah(n: Long): String {
    val s = n.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i])
        c++
        if (c % 3 == 0 && i != 0) sb.append('.')
    }
    return "Rp. " + sb.reverse().toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarisTransaksiScreen(
    title: String,
    isPembelian: Boolean,
    onBack: () -> Unit
) {
    val primary = Color(0xFF2F2FBF)
    val labelColor = Color(0xFF8A8A8A)

    // VM
    val repo = remember { InventarisRepository() }
    val vm: InventarisViewModel = viewModel(factory = InventarisViewModelFactory(repo))
    val ui by vm.uiState.collectAsState()

    // form state
    var tipePembayaran by remember { mutableStateOf(2) } // 1 tunai, 2 non tunai
    var tanggal by remember { mutableStateOf("") }

    var partyExpanded by remember { mutableStateOf(false) }
    var selectedPartyId by remember { mutableStateOf<Int?>(null) }

    var noTransaksi by remember { mutableStateOf("") } // pembelian only

    var biayaOngkir by remember { mutableStateOf("0") }
    var diskon by remember { mutableStateOf("0") } // penjualan only
    var pakaiPpn by remember { mutableStateOf(false) }

    val rows = remember { mutableStateListOf(ItemForm()) }
    var barangExpandedIndex by remember { mutableStateOf<Int?>(null) }

    // snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(ui.successMessage, ui.errorMessage) {
        ui.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
        ui.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessage()
        }
    }

    // load party + initial barang
    LaunchedEffect(isPembelian) {
        vm.loadParties(isPembelian)
        if (!isPembelian) {
            vm.loadBarang(isPembelian = false, pemasokId = null) // penjualan: load semua barang
        } else {
            vm.loadBarang(isPembelian = true, pemasokId = null) // pembelian: kosong dulu
        }
    }

    // kalau pembelian: ketika pilih pemasok -> load barang by pemasok
    LaunchedEffect(isPembelian, selectedPartyId) {
        if (isPembelian) {
            vm.loadBarang(isPembelian = true, pemasokId = selectedPartyId)
            // reset pilihan barang biar gak nyangkut barang dari pemasok lain
            rows.clear()
            rows.add(ItemForm())
        }
    }

    fun barangById(id: Int?): BarangDto? {
        if (id == null) return null
        return ui.barang.firstOrNull { it.idBarang == id }
    }

    fun rowSubtotal(row: ItemForm): Long {
        val qty = digitsToDouble(row.qty)
        val hargaPakai = if (isPembelian) row.hargaBeli.toDouble() else digitsToLong(row.hargaJual).toDouble()
        return (qty * hargaPakai).roundToLong()
    }

    val subtotalBarang by remember {
        derivedStateOf { rows.sumOf { rowSubtotal(it) } }
    }

    val diskonNominal by remember(isPembelian, diskon) {
        derivedStateOf { if (isPembelian) 0L else digitsToLong(diskon) }
    }

    val ongkirNominal by remember(biayaOngkir) {
        derivedStateOf { digitsToLong(biayaOngkir) }
    }

    val afterDiskon by remember(subtotalBarang, diskonNominal) {
        derivedStateOf { (subtotalBarang - diskonNominal).coerceAtLeast(0L) }
    }

    val ppnNominal by remember(pakaiPpn, afterDiskon) {
        derivedStateOf { if (!pakaiPpn) 0L else (afterDiskon * 0.11).roundToLong() }
    }

    val grandTotal by remember(afterDiskon, ppnNominal, ongkirNominal) {
        derivedStateOf { afterDiskon + ppnNominal + ongkirNominal }
    }

    val partyList = remember(isPembelian, ui.pemasok, ui.pelanggan) {
        if (isPembelian) {
            ui.pemasok.map { it.idPemasok to (it.namaPemasok) }
        } else {
            ui.pelanggan.map { it.idPelanggan to (it.namaPelanggan) }
        }
    }

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
                        enabled = !ui.loading,
                        onClick = {
                            // ===== validasi =====
                            if (tanggal.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Tanggal wajib diisi") }
                                return@Button
                            }
                            if (selectedPartyId == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isPembelian) "Pemasok wajib dipilih" else "Pelanggan wajib dipilih"
                                    )
                                }
                                return@Button
                            }
                            if (isPembelian && noTransaksi.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("No transaksi wajib diisi (Pembelian)") }
                                return@Button
                            }

                            val validItem = rows.any { it.barangId != null && digitsToDouble(it.qty) > 0.0 }
                            if (!validItem) {
                                scope.launch { snackbarHostState.showSnackbar("Minimal 1 item harus diisi") }
                                return@Button
                            }

                            // ===== build payload sesuai backend =====
                            val items = rows.mapNotNull { r ->
                                val id = r.barangId ?: return@mapNotNull null
                                val qty = digitsToDouble(r.qty)
                                if (qty <= 0.0) return@mapNotNull null

                                val hargaBeli = r.hargaBeli
                                val hargaJual = digitsToLong(r.hargaJual)
                                val hargaPakai = if (isPembelian) hargaBeli else hargaJual
                                val subtotal = (qty * hargaPakai.toDouble()).roundToLong()

                                InventarisStoreItemRequest(
                                    barangId = id,
                                    qty = qty, // sudah Double
                                    satuan = r.satuan.ifBlank { null },


                                    harga = hargaBeli.toDouble(),
//                                    hargajual = hargaJual.toDouble(),
                                    subtotal = subtotal.toDouble(),
                                    hargaMentah = hargaPakai.toDouble()
                                )
                            }

                            val req = InventarisStoreRequest(
                                tipe = if (isPembelian) "Inventaris" else "Penjualan",
                                tipePembayaran = tipePembayaran,
                                tanggal = tanggal,
                                partyId = selectedPartyId,
                                pelangganId = null,
                                noTransaksi = if (isPembelian) noTransaksi else null,

                                // ✅ KONVERSI
                                biayaLain = ongkirNominal.toDouble(),
                                diskonNominal = diskonNominal.toDouble(),
                                pajakPersen = 11.0,     // kalau DTO kamu Double
                                applyPajak = pakaiPpn,

                                items = items
                            )


                            vm.submitInventaris(req)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        if (ui.loading) {
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

        // ✅ scroll aman: pakai LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }

            // Tipe pembayaran
            item {
                Text("Tipe Pembayaran", fontSize = 12.sp, color = labelColor)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = tipePembayaran == 1,
                        onClick = { tipePembayaran = 1 },
                        label = { Text("Tunai") }
                    )
                    FilterChip(
                        selected = tipePembayaran == 2,
                        onClick = { tipePembayaran = 2 },
                        label = { Text("Non Tunai") }
                    )
                }
            }

            // Tanggal
            item {
                Text("Tanggal", fontSize = 12.sp, color = labelColor)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    singleLine = true,
                    placeholder = { Text("yyyy-mm-dd atau dd/mm/yyyy") }
                )
            }

            // Party dropdown (pemasok/pelanggan)
            item {
                Text(if (isPembelian) "Pemasok" else "Pelanggan", fontSize = 12.sp, color = labelColor)

                ExposedDropdownMenuBox(
                    expanded = partyExpanded,
                    onExpandedChange = { partyExpanded = !partyExpanded }
                ) {
                    val selectedLabel = partyList.firstOrNull { it.first == selectedPartyId }?.second ?: ""

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // ✅ INI BIAR DROPDOWN MUNCUL
                        value = selectedLabel,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(partyExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = partyExpanded,
                        onDismissRequest = { partyExpanded = false }
                    ) {
                        if (partyList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Belum ada data") },
                                onClick = { partyExpanded = false }
                            )
                        } else {
                            partyList.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.second) },
                                    onClick = {
                                        selectedPartyId = opt.first
                                        partyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // No transaksi pembelian
            if (isPembelian) {
                item {
                    Text("No Transaksi", fontSize = 12.sp, color = labelColor)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = noTransaksi,
                        onValueChange = { noTransaksi = it },
                        singleLine = true
                    )
                }
            }

            item { Divider() }

            // tombol tambah baris
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { rows.add(ItemForm()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tambah Baris")
                    }
                }
            }

            // rows item
            itemsIndexed(rows) { index, row ->
                val expanded = (barangExpandedIndex == index)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Item ${index + 1}", fontWeight = FontWeight.SemiBold)
                            IconButton(
                                onClick = { if (rows.size > 1) rows.removeAt(index) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }

                        // barang dropdown
                        Text("Barang", fontSize = 12.sp, color = labelColor)

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                barangExpandedIndex = if (expanded) null else index
                            }
                        ) {
                            val selectedBarangLabel = barangById(row.barangId)?.namaBarang ?: ""

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(), // ✅ INI BIAR DROPDOWN MUNCUL
                                value = selectedBarangLabel,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Pilih barang") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { barangExpandedIndex = null }
                            ) {
                                if (ui.barang.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text(if (isPembelian) "Pilih pemasok dulu" else "Belum ada data barang") },
                                        onClick = { barangExpandedIndex = null }
                                    )
                                } else {
                                    ui.barang.forEach { b ->
                                        DropdownMenuItem(
                                            text = { Text(b.namaBarang) },
                                            onClick = {
                                                val newRow = row.copy(
                                                    barangId = b.idBarang,
                                                    satuan = b.satuanUkur ?: "",
                                                    hargaBeli = b.hargaSatuan ?: 0L,
                                                    hargaJual = (b.hargaJual ?: 0L).toString(),
                                                    qty = if (digitsToDouble(row.qty) <= 0.0) "1" else row.qty
                                                )
                                                rows[index] = newRow
                                                barangExpandedIndex = null
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // qty
                        Text("Qty", fontSize = 12.sp, color = labelColor)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = row.qty,
                            onValueChange = { v -> rows[index] = row.copy(qty = v) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            placeholder = { Text("0") }
                        )

                        // satuan
                        Text("Satuan Ukur", fontSize = 12.sp, color = labelColor)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = row.satuan,
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            placeholder = { Text("-") }
                        )

                        // harga beli
                        Text("Harga Satuan Beli", fontSize = 12.sp, color = labelColor)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = formatRupiah(row.hargaBeli),
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true
                        )

                        // harga jual (editable)
                        Text("Harga Satuan Jual", fontSize = 12.sp, color = labelColor)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = row.hargaJual,
                            onValueChange = { v -> rows[index] = row.copy(hargaJual = v) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("0") }
                        )

                        val sub = rowSubtotal(rows[index])
                        Text("Total", fontSize = 12.sp, color = labelColor)
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = formatRupiah(sub),
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true
                        )
                    }
                }
            }

            item { Divider() }

            // ringkasan
            item {
                Text("Ringkasan", fontWeight = FontWeight.SemiBold)

                Text("Biaya Ongkir", fontSize = 12.sp, color = labelColor)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = biayaOngkir,
                    onValueChange = { biayaOngkir = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0") }
                )

                if (!isPembelian) {
                    Spacer(Modifier.height(8.dp))
                    Text("Diskon (Nominal)", fontSize = 12.sp, color = labelColor)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = diskon,
                        onValueChange = { diskon = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("0") }
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pakai PPN 11%")
                    Switch(checked = pakaiPpn, onCheckedChange = { pakaiPpn = it })
                }

                Text("PPN (11%)", fontSize = 12.sp, color = labelColor)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = formatRupiah(ppnNominal),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true
                )

                Text("Grand Total", fontSize = 12.sp, color = labelColor)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = formatRupiah(grandTotal),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true
                )
            }
        }
    }
}
