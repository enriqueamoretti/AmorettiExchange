package dev.eamoretti.amorettiexchange.presentation.clients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientsUiState(
    val isLoading: Boolean = false,
    val clients: List<Cliente> = emptyList(),
    val error: String? = null
)

class ClientsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        // Al iniciar, usa caché (force = false)
        fetchClients(force = false)
    }

    // ESTA ES LA FUNCIÓN PARA EL BOTÓN REFRESH
    fun refresh() {
        // Limpiamos la memoria global para que Transacciones también se actualice luego
        DataRepository.invalidarCacheGlobal()
        fetchClients(force = true)
    }

    private fun fetchClients(force: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Pasamos el parámetro 'force' al repositorio
                val listaClientes = DataRepository.obtenerClientes(forzarRecarga = force)

                _uiState.update {
                    it.copy(isLoading = false, clients = listaClientes)
                }

            } catch (e: Exception) {
                Log.e("Azure", "Error en ViewModel", e)
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            }
        }
    }
}