package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.repository.DataRepository // Importamos el Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaccion> = emptyList(), // Lista filtrada para la UI
    val error: String? = null,
    val filterType: Int? = null
)

class TransactionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    // Guardamos la lista maestra completa en el ViewModel también para filtrar rápido
    private var allTransactions: List<Transaccion> = emptyList()
    private var currentSearchQuery = ""

    init {
        fetchTransactions()
    }

    fun onFilterChanged(newFilter: Int?) {
        _uiState.update { it.copy(filterType = newFilter) }
        applyFilters() // Filtramos en memoria local (CERO COSTO)
    }

    fun onSearch(query: String) {
        currentSearchQuery = query
        applyFilters() // Filtramos en memoria local (CERO COSTO)
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Pedimos la lista completa al Repositorio
                allTransactions = DataRepository.obtenerTransacciones()

                // Aplicamos los filtros iniciales
                applyFilters()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Esta función filtra la lista que ya tenemos en RAM. No va a Azure.
    private fun applyFilters() {
        val filtered = allTransactions.filter { t ->
            // 1. Filtro por Tipo (Compra/Venta)
            val typeMatch = _uiState.value.filterType == null ||
                    (_uiState.value.filterType == 1 && t.tipoMovimiento == "Compra") ||
                    (_uiState.value.filterType == 2 && t.tipoMovimiento == "Venta")

            // 2. Filtro por Buscador
            val searchMatch = currentSearchQuery.isEmpty() ||
                    t.nombreCliente.contains(currentSearchQuery, ignoreCase = true)

            typeMatch && searchMatch
        }

        _uiState.update {
            it.copy(isLoading = false, transactions = filtered)
        }
    }
}