package dev.eamoretti.amorettiexchange.presentation.clients

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService
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

    // NOTA: Como tu API es "Anonymous", NO necesitas ninguna FUNCTION_KEY aquí.

    private val service = ApiClient.retrofit.create(CambistaService::class.java)
    private val gson = Gson()

    init {
        fetchClients()
    }

    fun fetchClients() {
        viewModelScope.launch {
            // 1. Marcar como cargando
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 2. Preparar la petición
                val request = ApiRequest(
                    operation = "SP_ListarClientes",
                    payload = mapOf("Busqueda" to "")
                )

                // 3. Llamar a la API (Sin clave)
                val response = service.ejecutarOperacion(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    // 4. Convertir la respuesta genérica a Lista de Clientes
                    val dataJson = gson.toJson(response.body()!!.data)
                    val tipoLista = object : TypeToken<List<Cliente>>() {}.type
                    val listaClientes: List<Cliente> = gson.fromJson(dataJson, tipoLista)

                    _uiState.update {
                        it.copy(isLoading = false, clients = listaClientes)
                    }
                    Log.d("Azure", "Clientes cargados: ${listaClientes.size}")

                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Error del servidor: ${response.message()}")
                    }
                }

            } catch (e: Exception) {
                Log.e("Azure", "Error al cargar clientes", e)
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Error de conexión")
                }
            }
        }
    }
}