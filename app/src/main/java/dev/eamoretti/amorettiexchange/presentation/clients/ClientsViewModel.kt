package dev.eamoretti.amorettiexchange.presentation.clients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.repository.DataRepository // Importamos el Repo
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
        fetchClients()
    }

    fun fetchClients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // CAMBIO CLAVE: Pedimos los datos al Repositorio, no a la API directa
                // El repositorio decidirá si usa caché o internet
                val listaClientes = DataRepository.obtenerClientes()

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