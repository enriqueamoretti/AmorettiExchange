package dev.eamoretti.amorettiexchange.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.eamoretti.amorettiexchange.presentation.auth.LoginScreen
import dev.eamoretti.amorettiexchange.presentation.clients.RegisterClientScreen
import dev.eamoretti.amorettiexchange.presentation.home.HomeScreen
import dev.eamoretti.amorettiexchange.presentation.transactions.RegisterTransactionScreen
import dev.eamoretti.amorettiexchange.presentation.agent.AgentChatScreen // IMPORTANTE

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // Estado global de la pantalla actual para el menú lateral
    var currentScreen: AppScreen by remember { mutableStateOf(AppScreen.Clients) }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        // PANTALLA DE LOGIN
        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Navegar al Home y borrar el Login del historial (back stack)
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // PANTALLA PRINCIPAL (HOME CON MENÚ LATERAL)
        composable(AppScreen.Home.route) {
            HomeScreen(
                currentScreen = currentScreen,
                onScreenChange = { newScreen -> currentScreen = newScreen },
                onLogout = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                },
                // Navegación a pantallas de registro
                onNavigateToRegisterClient = { navController.navigate(AppScreen.RegisterClient.route) },
                onNavigateToRegisterTransaction = {
                    // Antes de navegar, marcamos que estamos en el contexto de Transacciones
                    currentScreen = AppScreen.Transactions
                    navController.navigate(AppScreen.RegisterTransaction.route)
                },
                onNavigateToAgent = { navController.navigate(AppScreen.AgentChat.route) }
            )
        }

        // PANTALLA DE REGISTRO DE CLIENTE
        composable(AppScreen.RegisterClient.route) {
            RegisterClientScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // PANTALLA DE REGISTRO DE TRANSACCIÓN
        composable(AppScreen.RegisterTransaction.route) {
            RegisterTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // PANTALLA DEL AGENTE IA (NUEVO)
        composable(AppScreen.AgentChat.route) {
            AgentChatScreen(navController = navController)
        }
    }
}

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Home : AppScreen("home")

    // Sub-pantallas del Home
    object Clients : AppScreen("clients")
    object Transactions : AppScreen("transactions")
    object MonthlyBalancing : AppScreen("monthly_balancing")

    // Pantallas independientes
    object RegisterClient : AppScreen("register_client")
    object RegisterTransaction : AppScreen("register_transaction")
    object AgentChat : AppScreen("agent_chat") // RUTA NUEVA
}