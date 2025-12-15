package dev.eamoretti.amorettiexchange.presentation.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    var isSuccess by mutableStateOf(false)

    fun validateAndSave() {
        // 1. Validaciones Locales
        isNameError = name.isBlank()
        isPhoneError = mainPhone.isNotBlank() && mainPhone.length < 7 // Ajuste simple

        if (isNameError || isPhoneError) return

        // 2. Enviar a la API
        saveClientToApi()
    }

    private fun saveClientToApi() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val exito = DataRepository.guardarCliente(
                    razonSocial = name,
                    ruc = ruc.ifBlank { null },
                    tel = mainPhone.ifBlank { null },
                    aux = auxPhone.ifBlank { null },
                    cta = accountNumber.ifBlank { null },
                    dir = address.ifBlank { null }
                )

                if (exito) {
                    isSuccess = true
                }

            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        isSuccess = false
        errorMessage = null
    }
}