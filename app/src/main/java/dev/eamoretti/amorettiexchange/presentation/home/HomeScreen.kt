package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
    onNavigateToAgent: () -> Unit // Callback para el agente
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onMenuItemClick = { newScreen ->
                    // --- CORRECCIÓN AQUÍ ---
                    // Si el usuario toca "Agente IA", navegamos fuera del Home hacia la pantalla del chat.
                    // Si toca cualquier otra cosa (Clientes, Transacciones), cambiamos la vista interna.
                    if (newScreen == AppScreen.AgentChat) {
                        scope.launch { drawerState.close() }
                        onNavigateToAgent()
                    } else {
                        onScreenChange(newScreen)
                        scope.launch { drawerState.close() }
                    }
                },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLogout = onLogout
            )
        }
    ) {
        val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }

        Scaffold { paddingValues ->
            // El paddingValues se ignora deliberadamente aquí porque las pantallas hijas
            // (ClientsScreen, etc.) tienen su propio Scaffold con TopBar y manejan sus propios márgenes.

            when (currentScreen) {
                AppScreen.Clients -> ClientsScreen(
                    onMenuClick = onMenuClick,
                    onNavigateToRegisterClient = onNavigateToRegisterClient,
                    onAgentClick = onNavigateToAgent
                )
                AppScreen.Transactions -> TransactionsScreen(
                    onMenuClick = onMenuClick,
                    onNavigateToRegisterTransaction = onNavigateToRegisterTransaction,
                    onAgentClick = onNavigateToAgent
                )
                AppScreen.MonthlyBalancing -> MonthlyBalancingScreen(
                    onMenuClick = onMenuClick,
                    onAgentClick = onNavigateToAgent
                )
                // Default/fallback case
                else -> ClientsScreen(
                    onMenuClick = onMenuClick,
                    onNavigateToRegisterClient = onNavigateToRegisterClient,
                    onAgentClick = onNavigateToAgent
                )
            }
        }
    }
}