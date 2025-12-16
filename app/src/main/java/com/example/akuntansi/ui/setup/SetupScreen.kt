package com.example.akuntansi.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(onBack: () -> Unit) {
    var akun by remember { mutableStateOf("1101 - Kas") }
    var tanggal by remember { mutableStateOf("11 / 12 / 2025") }
    var nominal by remember { mutableStateOf("80.000") }

    val akunList = listOf("1101 - Kas", "1102 - Bank", "1201 - Piutang")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup") },
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

            Text("Nama Akun", fontSize = 12.sp, color = Color(0xFF8A8A8A))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = akun,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    akunList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                akun = item
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
        }
    }
}
