package com.example.akuntansi.ui.transaksi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class TransaksiTab(val label: String) {
    PEMBELIAN("Pembelian"),
    PENJUALAN("Penjualan"),
    KAS_BANK("Kas & Bank")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiScreen(onBack: () -> Unit) {

    val primary = Color(0xFF2F2FBF)
    val pillBg = Color(0xFFF0F0F6)

    var tab by remember { mutableStateOf(TransaksiTab.KAS_BANK) }

    // form state
    var expanded by remember { mutableStateOf(false) }
    var tipeTransaksi by remember { mutableStateOf("Bayar Gaji") }
    var tanggal by remember { mutableStateOf("11 / 12 / 2025") }
    var nominal by remember { mutableStateOf("80.000") }
    var keterangan by remember { mutableStateOf("") }

    // contoh tipe transaksi per tab
    val tipeList = remember(tab) {
        when (tab) {
            TransaksiTab.PEMBELIAN -> listOf("Pembelian Barang", "Biaya Kirim", "Retur Pembelian")
            TransaksiTab.PENJUALAN -> listOf("Penjualan Barang", "Diskon", "Retur Penjualan")
            TransaksiTab.KAS_BANK -> listOf("Bayar Gaji", "Tarik Tunai", "Setor Bank", "Biaya Admin Bank")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ✅ Tabs pill
            PillTabs(
                tab = tab,
                onSelect = { tab = it },
                primary = primary,
                pillBg = pillBg
            )

            // ✅ Form
            Text("Tipe Transaksi", fontSize = 12.sp, color = Color(0xFF8A8A8A))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = tipeTransaksi,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tipeList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                tipeTransaksi = item
                                expanded = false
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
                singleLine = true
            )

            Text("Nominal", fontSize = 12.sp, color = Color(0xFF8A8A8A))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = "Rp. $nominal",
                onValueChange = { newValue ->
                    nominal = newValue.replace("Rp.", "").trim()
                },
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
        }
    }
}

@Composable
private fun PillTabs(
    tab: TransaksiTab,
    onSelect: (TransaksiTab) -> Unit,
    primary: Color,
    pillBg: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(pillBg, RoundedCornerShape(999.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PillTabItem(
            text = TransaksiTab.PEMBELIAN.label,
            selected = tab == TransaksiTab.PEMBELIAN,
            primary = primary,
            modifier = Modifier.weight(1f)
        ) { onSelect(TransaksiTab.PEMBELIAN) }

        PillTabItem(
            text = TransaksiTab.PENJUALAN.label,
            selected = tab == TransaksiTab.PENJUALAN,
            primary = primary,
            modifier = Modifier.weight(1f)
        ) { onSelect(TransaksiTab.PENJUALAN) }

        PillTabItem(
            text = TransaksiTab.KAS_BANK.label,
            selected = tab == TransaksiTab.KAS_BANK,
            primary = primary,
            modifier = Modifier.weight(1f)
        ) { onSelect(TransaksiTab.KAS_BANK) }
    }
}

@Composable
private fun PillTabItem(
    text: String,
    selected: Boolean,
    primary: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) primary else Color.Transparent
    val fg = if (selected) Color.White else Color(0xFF555555)

    Box(
        modifier = modifier
            .height(34.dp)
            .background(bg, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
