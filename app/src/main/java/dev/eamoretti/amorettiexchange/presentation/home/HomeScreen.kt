package dev.eamoretti.amorettiexchange.presentation.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.eamoretti.amorettiexchange.presentation.clients.ClientsScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onLogout = onLogout,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold { paddingValues ->
            ClientsScreen(
                onMenuClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}