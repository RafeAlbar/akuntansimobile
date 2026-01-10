package com.example.akuntansi.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.akuntansi.data.model.setup.AkunDto
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onBack: () -> Unit,
    vm: SetupViewModel = viewModel()
) {
    // ✅ Simpan akun sebagai object biar dapet id juga
    var akunSelected by remember { mutableStateOf<AkunDto?>(null) }

    var tanggal by remember { mutableStateOf("11 / 12 / 2025") }
    var nominal by remember { mutableStateOf("80.000") }

    var expanded by remember { mutableStateOf(false) }

    // ✅ Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ✅ Load data akun pertama kali screen dibuka
    LaunchedEffect(Unit) {
        vm.loadAkun()
    }

    // ✅ Kalau ada error, tampilkan snackbar
    LaunchedEffect(vm.error) {
        vm.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // ✅ List akun dari API
    val akunList = vm.akunItems

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                onExpandedChange = {
                    // jangan buka menu kalau masih loading / list kosong
                    if (!vm.loading && akunList.isNotEmpty()) {
                        expanded = !expanded
                    }
                }
            ) {
                val displayText = when {
                    vm.loading -> "Memuat akun..."
                    akunSelected != null -> "${akunSelected!!.kode_akun} - ${akunSelected!!.nama_akun}"
                    else -> "Pilih akun"
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = displayText,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !vm.loading && akunList.isNotEmpty(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    akunList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text("${item.kode_akun} - ${item.nama_akun}") },
                            onClick = {
                                akunSelected = item
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
                    nominal = newValue.replace("Rp.", "", ignoreCase = true).trim()
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        val akun = akunSelected
                        if (akun == null) {
                            snackbarHostState.showSnackbar("Pilih akun dulu")
                            return@launch
                        }

                        vm.storeSaldoAwal(
                            userId = 1L,
                            akunId = akun.id,
                            tanggalUi = tanggal,
                            nominalUi = nominal,
                            onSuccess = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
                            onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                        )
                    }
                },
                enabled = !vm.saving, // biar gak dobel klik saat request jalan
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (vm.saving) "Menyimpan..." else "Simpan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }


            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
