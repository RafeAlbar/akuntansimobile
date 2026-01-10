package com.example.akuntansi.ui.transaksi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TransaksiTab(val label: String) {
    PEMBELIAN("Pembelian"),
    PENJUALAN("Penjualan"),
    KAS_BANK("Kas & Bank")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiRootScreen(
    onBack: () -> Unit
) {
    val primary = Color(0xFF2F2FBF)
    val pillBg = Color(0xFFF0F0F6)

    var tab by remember { mutableStateOf(TransaksiTab.KAS_BANK) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Transaksi") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                        }
                    }
                )

                // ✅ tabs tepat di bawah tombol back (TopAppBar)
                PillTabs(
                    tab = tab,
                    onSelect = { tab = it },
                    primary = primary,
                    pillBg = pillBg
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                TransaksiTab.PEMBELIAN -> {
                    InventarisTransaksiScreen(
                        title = "Pembelian",
                        isPembelian = true,
                        onBack = onBack // opsional, lihat catatan di bawah
                    )
                }

                TransaksiTab.PENJUALAN -> {
                    InventarisTransaksiScreen(
                        title = "Penjualan",
                        isPembelian = false,
                        onBack = onBack // opsional
                    )
                }

                TransaksiTab.KAS_BANK -> {
                    KasBankScreen(onBack = onBack) // opsional
                }
            }
        }
    }
}

@Composable
internal fun PillTabs(
    tab: TransaksiTab,
    onSelect: (TransaksiTab) -> Unit,
    primary: Color,
    pillBg: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
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
internal fun PillTabItem(
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
