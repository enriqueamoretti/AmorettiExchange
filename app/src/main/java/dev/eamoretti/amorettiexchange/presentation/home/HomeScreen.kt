package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import dev.eamoretti.amorettiexchange.presentation.clients.ClientsScreen
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.MonthlyBalancingScreen
import dev.eamoretti.amorettiexchange.presentation.navigation.AppScreen
import dev.eamoretti.amorettiexchange.presentation.transactions.TransactionsScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    currentScreen: AppScreen,
    onScreenChange: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    onNavigateToRegisterClient: () -> Unit,
    onNavigateToRegisterTransaction: () -> Unit,
    onNavigateToAgent: () -> Unit // Callback nuevo
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onMenuItemClick = { newScreen ->
                    onScreenChange(newScreen)
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLogout = onLogout
            )
        }
    ) {
        val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }

        Scaffold(
            floatingActionButton = {
                // Botón Flotante para el Agente (Siempre visible en el Home)
                FloatingActionButton(
                    onClick = onNavigateToAgent,
                    containerColor = Color(0xFFD4AF37), // Dorado Amoretti
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Chat IA"
                    )
                }
            }
        ) { paddingValues ->
            // El contenido de la pantalla respeta el padding del Scaffold si es necesario,
            // pero tus pantallas internas ya manejan su propio layout.
            // Simplemente llamamos a la pantalla correspondiente.

            when (currentScreen) {
                AppScreen.Clients -> ClientsScreen(onMenuClick, onNavigateToRegisterClient)
                AppScreen.Transactions -> TransactionsScreen(onMenuClick, onNavigateToRegisterTransaction)
                AppScreen.MonthlyBalancing -> MonthlyBalancingScreen(onMenuClick)
                // Default/fallback case
                else -> ClientsScreen(onMenuClick, onNavigateToRegisterClient)
            }
        }
    }
}