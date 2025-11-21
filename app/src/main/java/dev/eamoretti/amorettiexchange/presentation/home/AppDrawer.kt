package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppDrawer(
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = Color(0xFF092B5A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Menú", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onCloseDrawer) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar Menú", tint = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))

            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray, shape = androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("EA", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Eladio Enrique", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Amoretti Malpartida", color = Color.White)
                    Text("Administrador", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            // Amoretti Exchange Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFF0A1A2F), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text("Amoretti Exchange", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Sistema de Operaciones", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Navigation Items
            NavigationDrawerItem(
                label = { Text("Clientes") },
                selected = true,
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.White,
                    unselectedContainerColor = Color.Transparent,
                    selectedTextColor = Color(0xFF092B5A),
                    unselectedTextColor = Color.White,
                    selectedIconColor = Color(0xFF092B5A),
                    unselectedIconColor = Color.White
                )
            )
            NavigationDrawerItem(
                label = { Text("Transacciones") },
                selected = false,
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Transacciones") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.White,
                    unselectedContainerColor = Color.Transparent,
                    selectedTextColor = Color(0xFF092B5A),
                    unselectedTextColor = Color.White,
                    selectedIconColor = Color(0xFF092B5A),
                    unselectedIconColor = Color.White
                )
            )
            NavigationDrawerItem(
                label = { Text("Cuadre Mensual") },
                selected = false,
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Cuadre Mensual") },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.White,
                    unselectedContainerColor = Color.Transparent,
                    selectedTextColor = Color(0xFF092B5A),
                    unselectedTextColor = Color.White,
                    selectedIconColor = Color(0xFF092B5A),
                    unselectedIconColor = Color.White
                )
            )

            Spacer(Modifier.weight(1f))

            // Logout
            Divider(color = Color.White.copy(alpha = 0.2f))
            NavigationDrawerItem(
                label = { Text("Cerrar Sesión", color = Color.Red) },
                selected = false,
                onClick = onLogout,
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.Red) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                )
            )

            // Footer
            Text(
                "v1.0.0 - Amoretti Exchange",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )
        }
    }
}