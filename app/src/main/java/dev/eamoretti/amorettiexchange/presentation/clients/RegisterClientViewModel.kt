package dev.eamoretti.amorettiexchange.presentation.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.launch

class RegisterClientViewModel : ViewModel() {

    // Estados del Formulario
    var name by mutableStateOf("")
    var ruc by mutableStateOf("")
    var mainPhone by mutableStateOf("")
    var auxPhone by mutableStateOf("")
    var accountNumber by mutableStateOf("")
    var address by mutableStateOf("")

    // Estados de Error
    var isNameError by mutableStateOf(false)
    var isPhoneError by mutableStateOf(false)

    // Estado de carga y resultado
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false) // Para navegar atrás al terminar

    private val service = ApiClient.retrofit.create(CambistaService::class.java)

    fun validateAndSave() {
        // 1. Validaciones Locales
        isNameError = name.isBlank()
        isPhoneError = mainPhone.isNotBlank() && mainPhone.length != 9

        if (isNameError || isPhoneError) return

        // 2. Enviar a la API
        saveClientToApi()
    }

    private fun saveClientToApi() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Preparamos los parámetros para el SP_GestionarCliente
                // IMPORTANTE: Los nombres de las claves deben coincidir con los parámetros del SP en SQL (@RazonSocial, etc)
                // pero sin la arroba.
                val params = mapOf(
                    "RazonSocial" to name,
                    "DocumentoIdentidad" to ruc.ifBlank { null },
                    "TelefonoContacto" to mainPhone.ifBlank { null },
                    "TelefonoAuxiliar" to auxPhone.ifBlank { null },
                    "NumeroCuenta" to accountNumber.ifBlank { null },
                    "Direccion" to address.ifBlank { null }
                    // IdCliente no se envía (es NULL por defecto en el SP para crear uno nuevo)
                )

                val request = ApiRequest(
                    operation = "SP_GestionarCliente",
                    payload = params
                )

                val response = service.ejecutarOperacion(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    // Éxito: Limpiamos la caché global para que la lista de clientes se actualice
                    DataRepository.invalidarCacheGlobal()
                    isSuccess = true
                } else {
                    // Error del servidor (ej: DNI duplicado)
                    errorMessage = response.body()?.error ?: "Error al guardar cliente"
                }

            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Resetear estado al salir
    fun resetState() {
        isSuccess = false
        errorMessage = null
    }
}