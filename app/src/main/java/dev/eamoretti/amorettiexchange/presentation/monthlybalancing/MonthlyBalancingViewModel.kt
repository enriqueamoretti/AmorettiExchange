package dev.eamoretti.amorettiexchange.presentation.monthlybalancing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.ResumenMensual
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class MonthlyUiState(
    val isLoading: Boolean = false,
    val years: List<Int> = listOf(Calendar.getInstance().get(Calendar.YEAR)),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonthIndex: Int = Calendar.getInstance().get(Calendar.MONTH),

    val summary: ResumenMensual? = null,
    val purchaseMovements: List<Transaccion> = emptyList(),
    val saleMovements: List<Transaccion> = emptyList(),

    val error: String? = null
)

class MonthlyBalancingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyUiState())
    val uiState: StateFlow<MonthlyUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaccion> = emptyList()

    init {
        loadData(force = false)
    }

    // ESTA ES LA FUNCIÓN PARA EL BOTÓN REFRESH
    fun refresh() {
        DataRepository.invalidarCacheGlobal()
        loadData(force = true)
    }

    private fun loadData(force: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Pasamos el parámetro 'force' al repositorio
                allTransactions = DataRepository.obtenerTransacciones(forzarRecarga = force)

                // Recalcular años disponibles
                val availableYears = allTransactions
                    .map { it.fecha.take(4).toInt() }
                    .distinct()
                    .sortedDescending()

                if (availableYears.isNotEmpty()) {
                    val currentYear = _uiState.value.selectedYear
                    val newYear = if (availableYears.contains(currentYear)) currentYear else availableYears.first()

                    _uiState.update { it.copy(years = availableYears, selectedYear = newYear) }
                }

                calculateLocalReport()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onYearSelected(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        calculateLocalReport()
    }

    fun onMonthSelected(monthIndex: Int) {
        _uiState.update { it.copy(selectedMonthIndex = monthIndex) }
        calculateLocalReport()
    }

    private fun calculateLocalReport() {
        val year = _uiState.value.selectedYear
        val month = _uiState.value.selectedMonthIndex + 1

        val filtered = allTransactions.filter { t ->
            val tYear = t.fecha.take(4).toInt()
            val tMonth = t.fecha.substring(5, 7).toInt()
            tYear == year && tMonth == month
        }

        val purchases = filtered.filter { it.tipoMovimiento.equals("Compra", true) }
        val sales = filtered.filter { it.tipoMovimiento.equals("Venta", true) }

        val totalCompraUSD = purchases.sumOf { it.montoDivisa }
        val totalCompraSoles = purchases.sumOf { it.montoSoles }
        val totalVentaUSD = sales.sumOf { it.montoDivisa }
        val totalVentaSoles = sales.sumOf { it.montoSoles }
        val utilidad = totalVentaSoles - totalCompraSoles

        val tasas = filtered.map { it.montoSoles / it.montoDivisa }
        val tasaPromedio = if (tasas.isNotEmpty()) tasas.average() else 0.0

        val resumen = ResumenMensual(
            totalCompraUSD = totalCompraUSD,
            totalCompraSoles = totalCompraSoles,
            totalVentaUSD = totalVentaUSD,
            totalVentaSoles = totalVentaSoles,
            utilidad = utilidad,
            tasaPromedio = tasaPromedio
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                summary = resumen,
                purchaseMovements = purchases,
                saleMovements = sales
            )
        }
    }
}