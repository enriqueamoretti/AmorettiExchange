package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaccion> = emptyList(),
    val error: String? = null,
    val filterType: Int? = null
)

class TransactionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaccion> = emptyList()
    private var currentSearchQuery = ""

    init {
        fetchTransactions(force = false)
    }

    // ESTA ES LA FUNCIÓN PARA EL BOTÓN REFRESH
    fun refresh() {
        DataRepository.invalidarCacheGlobal()
        fetchTransactions(force = true)
    }

    fun onFilterChanged(newFilter: Int?) {
        _uiState.update { it.copy(filterType = newFilter) }
        applyFilters()
    }

    fun onSearch(query: String) {
        currentSearchQuery = query
        applyFilters()
    }

    private fun fetchTransactions(force: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Pasamos el parámetro 'force' al repositorio
                allTransactions = DataRepository.obtenerTransacciones(forzarRecarga = force)

                // Volvemos a aplicar los filtros con la data nueva
                applyFilters()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun applyFilters() {
        val filtered = allTransactions.filter { t ->
            val typeMatch = _uiState.value.filterType == null ||
                    (_uiState.value.filterType == 1 && t.tipoMovimiento == "Compra") ||
                    (_uiState.value.filterType == 2 && t.tipoMovimiento == "Venta")

            val searchMatch = currentSearchQuery.isEmpty() ||
                    t.nombreCliente.contains(currentSearchQuery, ignoreCase = true)

            typeMatch && searchMatch
        }

        _uiState.update {
            it.copy(isLoading = false, transactions = filtered)
        }
    }
}