package com.example.akuntansi.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.akuntansi.ui.components.BalanceCard
import com.example.akuntansi.ui.components.MenuItem
import com.example.akuntansi.ui.components.MenuTile

@Composable
fun HomeScreen(
    onOpenSetup: () -> Unit
) {
    val headerColor = Color(0xFF2F2FBF)
    val cardYellow = Color(0xFFF4B400)
    val surfaceWhite = Color(0xFFF6F6F8)

    var notifCount by remember { mutableIntStateOf(3) }
    val saldoAwal = 80000
    val saldoAkhir = 80000

    val menu = listOf(
        MenuItem("Setup", Icons.Default.Settings, Color(0xFF3D3DE0), onClick = onOpenSetup),
        MenuItem("Transaksi", Icons.Default.AttachMoney, Color(0xFFF4B400), onClick = { }),
        MenuItem("Laporan", Icons.Default.ReceiptLong, Color(0xFF3D3DE0), onClick = { }),
        MenuItem("Transaksi", Icons.Default.Apps, Color(0xFFE91E63), onClick = { }),
        MenuItem("Rekom BEP", Icons.Default.Calculate, Color(0xFF1DB954), onClick = { }),
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(headerColor)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B1B6B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    "Hi, Sekar Alvaro",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                BadgedBox(
                    badge = { if (notifCount > 0) Badge { Text(notifCount.toString()) } }
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notif", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = surfaceWhite,
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        BalanceCard("Saldo Awal", saldoAwal, cardYellow, Modifier.weight(1f))
                        BalanceCard("Saldo Akhir", saldoAkhir, cardYellow, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(18.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(menu) { item -> MenuTile(item) }
                    }
                }
            }
        }
    }
}
