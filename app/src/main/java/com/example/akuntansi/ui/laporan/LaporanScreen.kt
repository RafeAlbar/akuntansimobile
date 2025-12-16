package com.example.akuntansi.ui.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akuntansi.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(onBack: () -> Unit) {
    val amountColor = Color(0xFF2F2FBF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Keuangan") },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== Card: Laba Rugi =====
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Laba Rugi", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    RowItem("Penjualan Barang Dagang", 40000, amountColor)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Total Pendapatan", 40000, amountColor, bold = true)

                    Spacer(Modifier.height(12.dp))
                    RowItem("Beban Gaji", 10000, amountColor)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Beban Listrik", 15000, amountColor)
                    Spacer(Modifier.height(10.dp))
                    RowItem("Total Laba Rugi", 15000, amountColor, bold = true)
                }
            }

            // ===== Card: Neraca =====
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Neraca", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    SectionTitle("Aset")
                    Spacer(Modifier.height(8.dp))
                    RowItem("ATK", 40000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Kas", 40000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Persediaan Usaha", 10000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Persediaan", 15000, amountColor, small = true)
                    Spacer(Modifier.height(10.dp))
                    RowItem("Total Aset", 115000, amountColor, bold = true)

                    Spacer(Modifier.height(16.dp))
                    SectionTitle("Liabilitas")
                    Spacer(Modifier.height(8.dp))
                    RowItem("Utang Bank", 40000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Utang Usaha", 10000, amountColor, small = true)
                    Spacer(Modifier.height(10.dp))
                    RowItem("Total Liabilitas", 60000, amountColor, bold = true)

                    Spacer(Modifier.height(16.dp))
                    SectionTitle("Ekuitas")
                    Spacer(Modifier.height(8.dp))
                    RowItem("Modal", 40000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Prive", 10000, amountColor, small = true)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Saldo", 10000, amountColor, small = true)
                    Spacer(Modifier.height(10.dp))
                    RowItem("Total Ekuitas", 60000, amountColor, bold = true)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF444444))
}

@Composable
private fun RowItem(
    label: String,
    amount: Int,
    amountColor: Color,
    bold: Boolean = false,
    small: Boolean = false
) {
    val labelSize = if (small) 12.sp else 13.sp
    val amountSize = if (small) 12.sp else 13.sp
    val fw = if (bold) FontWeight.SemiBold else FontWeight.Normal
    val labelColor = if (small) Color(0xFF8A8A8A) else Color(0xFF444444)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = labelSize, color = labelColor, fontWeight = fw)
        Text("Rp. ${formatRupiah(amount)}", fontSize = amountSize, color = amountColor, fontWeight = fw)
    }
}
