package dev.eamoretti.amorettiexchange.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Asegúrate de que este import sea correcto
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false)

    init {
        // Esta línea es la que te daba error.
        // Si actualizaste DataRepository.kt, ahora debería funcionar.
        try {
            if (DataRepository.obtenerUsuarioSesion() != null) {
                isLoggedIn = true
            }
        } catch (e: Exception) {
            // Manejo silencioso si el repo no está inicializado aún (raro en el flujo normal)
        }
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Ingrese correo y contraseña"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val usuario = DataRepository.login(email, password)
                if (usuario != null) {
                    isLoggedIn = true
                } else {
                    loginError = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                loginError = e.message ?: "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }
}