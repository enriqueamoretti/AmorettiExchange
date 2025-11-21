package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import dev.eamoretti.amorettiexchange.presentation.clients.ClientsScreen
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.MonthlyBalancingScreen
import dev.eamoretti.amorettiexchange.presentation.navigation.AppScreen
import dev.eamoretti.amorettiexchange.presentation.transactions.TransactionsScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToRegisterClient: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Clients) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onLogout = onLogout,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                onMenuItemClick = { screen ->
                    currentScreen = screen
                    scope.launch {
                        drawerState.close()
                    }
                },
                currentScreen = currentScreen
            )
        }
    ) {
        // Explicitly defining the lambda's type to () -> Unit resolves the type mismatch error.
        // The compiler was getting confused by the `Job` return type of `scope.launch`.
        val onMenuClick: () -> Unit = {
            scope.launch { drawerState.open() }
        }

        // The `when` expression must be exhaustive. By handling all possible AppScreen states,
        // we satisfy the compiler and create more robust code.
        val screenContent: @Composable () -> Unit = when (currentScreen) {
            AppScreen.Clients -> { { ClientsScreen(onMenuClick, onNavigateToRegisterClient) } }
            AppScreen.Transactions -> { { TransactionsScreen(onMenuClick) } }
            AppScreen.MonthlyBalancing -> { { MonthlyBalancingScreen(onMenuClick) } }
            AppScreen.Login, AppScreen.Home, AppScreen.RegisterClient -> {
                { ClientsScreen(onMenuClick, onNavigateToRegisterClient) }
            }
        }

        screenContent()
    }
}