package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import dev.eamoretti.amorettiexchange.presentation.navigation.AppScreen

@Composable
fun AppDrawer(
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit,
    onMenuItemClick: (AppScreen) -> Unit,
    currentScreen: AppScreen
) {
    // Obtenemos el usuario de la sesión actual (Síncrono porque SharedPreferences es rápido)
    val usuario = remember { DataRepository.obtenerUsuarioSesion() }
    val nombreCompleto = usuario?.nombreCompleto ?: "Usuario Invitado"
    val email = usuario?.email ?: "Sin correo"

    // Lógica para las iniciales (Círculo EA)
    val iniciales = remember(nombreCompleto) {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotEmpty() }
        when {
            partes.isEmpty() -> "U"
            partes.size == 1 -> partes[0].take(2).uppercase()
            // Si hay 3 o más partes (ej: Eladio Enrique Amoretti Malpartida)
            // Tomamos la 1ra letra del 1er nombre (Eladio) y la 1ra del 3er apellido (Amoretti)
            partes.size >= 3 -> "${partes[0].first()}${partes[2].first()}".uppercase()
            // Si son 2 partes (Nombre Apellido)
            else -> "${partes[0].first()}${partes[1].first()}".uppercase()
        }
    }

    // Lógica para dividir nombres y apellidos (visual)
    val (nombresVisual, apellidosVisual) = remember(nombreCompleto) {
        val partes = nombreCompleto.trim().split(" ")
        if (partes.size >= 3) {
            // Ej: Eladio Enrique (Nombres) / Amoretti Malpartida (Apellidos)
            val corte = if (partes.size == 4) 2 else 1
            Pair(
                partes.take(corte).joinToString(" "),
                partes.drop(corte).joinToString(" ")
            )
        } else {
            Pair(nombreCompleto, "Usuario")
        }
    }

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = Color(0xFF092B5A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con X de cerrar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Menú", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onCloseDrawer) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar Menú", tint = Color.White)
                }
            }
            Spacer(Modifier.height(24.dp))

            // --- INFORMACIÓN DEL USUARIO ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Círculo de Iniciales
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF9CA3AF), CircleShape), // Gris como en tu imagen
                    contentAlignment = Alignment.Center
                ) {
                    Text(iniciales, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(Modifier.width(16.dp))

                // Textos de Nombre
                Column {
                    Text(nombresVisual, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(apellidosVisual, color = Color.White, fontSize = 15.sp)
                    Text(
                        text = "Administrador",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Amoretti Exchange Info Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFF0F3466), shape = RoundedCornerShape(8.dp)) // Azul un poco más oscuro
                    .padding(16.dp)
            ) {
                Text("Amoretti Exchange", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Sistema de Operaciones", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }

            Spacer(Modifier.height(32.dp))

            // --- ÍTEMS DE NAVEGACIÓN ---
            NavigationDrawerItem(
                label = { Text("Clientes", fontWeight = FontWeight.Medium) },
                selected = currentScreen == AppScreen.Clients,
                onClick = { onMenuItemClick(AppScreen.Clients) },
                icon = { Icon(Icons.Default.People, null) },
                colors = navigationDrawerColors(),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            NavigationDrawerItem(
                label = { Text("Transacciones", fontWeight = FontWeight.Medium) },
                selected = currentScreen == AppScreen.Transactions,
                onClick = { onMenuItemClick(AppScreen.Transactions) },
                icon = { Icon(Icons.Default.SwapHoriz, null) },
                colors = navigationDrawerColors(),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            // Botón blanco destacado para Cuadre Mensual
            NavigationDrawerItem(
                label = { Text("Cuadre Mensual", fontWeight = FontWeight.Medium) },
                selected = currentScreen == AppScreen.MonthlyBalancing,
                onClick = { onMenuItemClick(AppScreen.MonthlyBalancing) },
                icon = { Icon(Icons.Default.BarChart, null) },
                colors = if (currentScreen == AppScreen.MonthlyBalancing) {
                    NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.White,
                        selectedTextColor = Color(0xFF092B5A),
                        selectedIconColor = Color(0xFF092B5A)
                    )
                } else navigationDrawerColors(),
                modifier = Modifier.padding(vertical = 4.dp),
                shape = RoundedCornerShape(50) // Píldora completa
            )

            // --- AGENTE IA (NUEVO) ---
            Spacer(Modifier.height(8.dp))
            NavigationDrawerItem(
                label = { Text("Agente IA", fontWeight = FontWeight.Bold, color = Color(0xFFD4AF37)) },
                selected = currentScreen == AppScreen.AgentChat,
                onClick = { onMenuItemClick(AppScreen.AgentChat) },
                icon = { Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFD4AF37)) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFFD4AF37).copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent,
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(Modifier.weight(1f))

            // Logout
            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            NavigationDrawerItem(
                label = { Text("Cerrar Sesión", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) },
                selected = false,
                onClick = {
                    DataRepository.cerrarSesion() // Limpia datos locales
                    onLogout()
                },
                icon = { Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFFF5252)) },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                )
            )

            // Footer
            Text(
                "v1.0.0 - Amoretti Exchange",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun navigationDrawerColors() = NavigationDrawerItemDefaults.colors(
    selectedContainerColor = Color.White.copy(alpha = 0.1f),
    unselectedContainerColor = Color.Transparent,
    selectedTextColor = Color.White,
    unselectedTextColor = Color.White,
    selectedIconColor = Color.White,
    unselectedIconColor = Color.White
)