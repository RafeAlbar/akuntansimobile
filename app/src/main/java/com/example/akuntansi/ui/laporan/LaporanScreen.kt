package com.example.akuntansi.ui.laporan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.akuntansi.util.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    onBack: () -> Unit,
    vm: LaporanViewModel = viewModel()
) {
    val amountColor = Color(0xFF2F2FBF)

    // ✅ load saat screen dibuka
    LaunchedEffect(Unit) {
        vm.loadAll(userId = 0) // pattern kamu: tanpa login
    }

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

            if (vm.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            vm.error?.let { msg ->
                Text(msg, color = Color.Red, fontSize = 12.sp)
            }

            val labaRugi = vm.labaRugi
            val neraca = vm.neraca

            // ===== Card: Laba Rugi =====
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Laba Rugi", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    val penjualan = (labaRugi?.total_penjualan ?: 0.0).toLong()
                    val pendapatan = (labaRugi?.total_pendapatan ?: 0.0).toLong()
                    val beban = (labaRugi?.total_beban ?: 0.0).toLong()
                    val labaBersih = (labaRugi?.laba_bersih ?: 0.0).toLong()

                    RowItem("Total Penjualan", penjualan, amountColor)
                    Spacer(Modifier.height(6.dp))
                    RowItem("Total Pendapatan", pendapatan, amountColor, bold = true)

                    Spacer(Modifier.height(12.dp))
                    RowItem("Total Beban", beban, amountColor)
                    Spacer(Modifier.height(10.dp))
                    RowItem("Laba Bersih", labaBersih, amountColor, bold = true)
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

                    val asetList = neraca?.data?.aset ?: emptyList()
                    val liabList = neraca?.data?.liabilitas ?: emptyList()
                    val ekuList = neraca?.data?.ekuitas ?: emptyList()

                    val totalAset = asetList.sumOf { (it.saldo ?: 0.0) }.toLong()
                    val totalLiab = liabList.sumOf { (it.saldo ?: 0.0) }.toLong()
                    val totalEku = ekuList.sumOf { (it.saldo ?: 0.0) }.toLong()

                    SectionTitle("Aset")
                    Spacer(Modifier.height(8.dp))
                    asetList.take(6).forEach {
                        RowItem(
                            label = it.nama_akun ?: "-",
                            amount = (it.saldo ?: 0.0).toLong(),
                            amountColor = amountColor,
                            small = true
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                    RowItem("Total Aset", totalAset, amountColor, bold = true)

                    Spacer(Modifier.height(16.dp))
                    SectionTitle("Liabilitas")
                    Spacer(Modifier.height(8.dp))
                    liabList.take(6).forEach {
                        RowItem(
                            label = it.nama_akun ?: "-",
                            amount = (it.saldo ?: 0.0).toLong(),
                            amountColor = amountColor,
                            small = true
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                    RowItem("Total Liabilitas", totalLiab, amountColor, bold = true)

                    Spacer(Modifier.height(16.dp))
                    SectionTitle("Ekuitas")
                    Spacer(Modifier.height(8.dp))
                    ekuList.take(6).forEach {
                        RowItem(
                            label = it.nama_akun ?: "-",
                            amount = (it.saldo ?: 0.0).toLong(),
                            amountColor = amountColor,
                            small = true
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                    RowItem("Total Ekuitas", totalEku, amountColor, bold = true)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = Color(0xFF444444)
    )
}

@Composable
private fun RowItem(
    label: String,
    amount: Long,
    amountColor: Color,
    bold: Boolean = false,
    small: Boolean = false
) {
    val labelSize = if (small) 12.sp else 13.sp
    val amountSize = if (small) 12.sp else 13.sp
    val fw = if (bold) FontWeight.SemiBold else FontWeight.Normal
    val labelColor = if (small) Color(0xFF8A8A8A) else Color(0xFF444444)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = labelSize,
            color = labelColor,
            fontWeight = fw
        )
        Text(
            text = "Rp. ${formatRupiah(amount)}",
            fontSize = amountSize,
            color = amountColor,
            fontWeight = fw
        )
    }
}
