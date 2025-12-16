package com.example.akuntansi.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.akuntansi.navigation.Routes

data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomBar(currentRoute: String, onSelect: (String) -> Unit) {
    NavigationBar {
        val items = listOf(
            NavItem("Home", Icons.Default.Home, Routes.HOME),
            NavItem("Search", Icons.Default.Search, Routes.SEARCH),
            NavItem("Inbox", Icons.Default.Email, Routes.INBOX),
            NavItem("Settings", Icons.Default.Settings, Routes.SETTINGS),
        )

        items.forEach { item ->
            val selected = currentRoute == item.route || (currentRoute == Routes.SETUP && item.route == Routes.HOME)
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
