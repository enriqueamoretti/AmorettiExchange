package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import dev.eamoretti.amorettiexchange.presentation.clients.ClientsScreen
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.MonthlyBalancingScreen
import dev.eamoretti.amorettiexchange.presentation.navigation.AppScreen
import dev.eamoretti.amorettiexchange.presentation.transactions.TransactionsScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onLogout: () -> Unit
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
        // The error was caused by a non-exhaustive `when` statement.
        // The AppScreen sealed class has more states (Login, Home) than were being handled.
        // This can confuse the Compose compiler. By making the `when` statement
        // exhaustive, we handle all possible cases and resolve the error.
        when (currentScreen) {
            AppScreen.Clients -> ClientsScreen(onMenuClick = { scope.launch { drawerState.open() } })
            AppScreen.Transactions -> TransactionsScreen(onMenuClick = { scope.launch { drawerState.open() } })
            AppScreen.MonthlyBalancing -> MonthlyBalancingScreen(onMenuClick = { scope.launch { drawerState.open() } })
            AppScreen.Login, AppScreen.Home -> {
                // These states should not be reached while inside HomeScreen,
                // but we handle them to make the `when` exhaustive.
                // We'll default to showing the Clients screen as a fallback.
                ClientsScreen(onMenuClick = { scope.launch { drawerState.open() } })
            }
        }
    }
}