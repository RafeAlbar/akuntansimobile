package com.example.akuntansi.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.akuntansi.ui.components.BottomBar
import com.example.akuntansi.ui.home.HomeScreen
import com.example.akuntansi.ui.laporan.LaporanScreen
import com.example.akuntansi.ui.setup.SetupScreen
import com.example.akuntansi.ui.transaksi.TransaksiRootScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.HOME

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = currentRoute,
                onSelect = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenSetup = { navController.navigate(Routes.SETUP) },
                    onOpenTransaksi = { navController.navigate(Routes.TRANSAKSI) },
                    onOpenLaporan = { navController.navigate(Routes.LAPORAN) }
                )
            }

            composable(Routes.SETUP) {
                SetupScreen(onBack = { navController.popBackStack() })
            }

            // placeholder tab
            composable(Routes.SEARCH) {
                HomeScreen(
                    onOpenSetup = { navController.navigate(Routes.SETUP) },
                    onOpenTransaksi = { navController.navigate(Routes.TRANSAKSI) }
                )
            }
            composable(Routes.INBOX) {
                HomeScreen(
                    onOpenSetup = { navController.navigate(Routes.SETUP) },
                    onOpenTransaksi = { navController.navigate(Routes.TRANSAKSI) }
                )
            }
            composable(Routes.SETTINGS) {
                HomeScreen(
                    onOpenSetup = { navController.navigate(Routes.SETUP) },
                    onOpenTransaksi = { navController.navigate(Routes.TRANSAKSI) }
                )
            }

            // ✅ ini yang penting: buka ROOT (tabs)
            composable(Routes.TRANSAKSI) {
                TransaksiRootScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.LAPORAN) {
                LaporanScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
