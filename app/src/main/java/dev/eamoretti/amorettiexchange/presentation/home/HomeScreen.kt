package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import dev.eamoretti.amorettiexchange.presentation.clients.ClientsScreen
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
        Scaffold { paddingValues ->
            when (currentScreen) {
                AppScreen.Clients -> ClientsScreen(onMenuClick = { scope.launch { drawerState.open() } })
                AppScreen.Transactions -> TransactionsScreen(onMenuClick = { scope.launch { drawerState.open() } })
                else -> ClientsScreen(onMenuClick = { scope.launch { drawerState.open() } }) // Default screen
            }
        }
    }
}