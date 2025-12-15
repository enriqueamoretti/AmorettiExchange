package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
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
    onNavigateToRegisterTransaction: () -> Unit
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

        // The content of the screen is now determined by the state hoisted to the NavGraph
        when (currentScreen) {
            AppScreen.Clients -> ClientsScreen(onMenuClick, onNavigateToRegisterClient)
            AppScreen.Transactions -> TransactionsScreen(onMenuClick, onNavigateToRegisterTransaction)
            AppScreen.MonthlyBalancing -> MonthlyBalancingScreen(onMenuClick)
            // Default/fallback case
            else -> ClientsScreen(onMenuClick, onNavigateToRegisterClient)
        }
    }
}