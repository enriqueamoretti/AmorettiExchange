package dev.eamoretti.amorettiexchange.presentation.monthlybalancing

import android.util.Log
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

    fun refresh() {
        DataRepository.invalidarCacheGlobal()
        loadData(force = true)
    }

    private fun loadData(force: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Descargar datos
                allTransactions = DataRepository.obtenerTransacciones(forzarRecarga = force)
                Log.d("MonthlyVM", "Total transacciones descargadas: ${allTransactions.size}")

                // 2. Calcular años disponibles (Parseo seguro)
                val availableYears = allTransactions
                    .mapNotNull { parseYear(it.fecha) }
                    .distinct()
                    .sortedDescending()

                if (availableYears.isNotEmpty()) {
                    val currentYear = _uiState.value.selectedYear
                    val newYear = if (availableYears.contains(currentYear)) currentYear else availableYears.first()
                    _uiState.update { it.copy(years = availableYears, selectedYear = newYear) }
                }

                // 3. Calcular reporte
                calculateLocalReport()

            } catch (e: Exception) {
                Log.e("MonthlyVM", "Error cargando datos", e)
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
        val selectedYear = _uiState.value.selectedYear
        val selectedMonth = _uiState.value.selectedMonthIndex + 1 // 1..12

        // 1. FILTRADO ROBUSTO
        val filtered = allTransactions.filter { t ->
            val dateParts = parseDateParts(t.fecha)
            if (dateParts != null) {
                val (tYear, tMonth) = dateParts
                tYear == selectedYear && tMonth == selectedMonth
            } else {
                false // Fecha inválida, ignorar
            }
        }

        Log.d("MonthlyVM", "Transacciones del mes ($selectedMonth/$selectedYear): ${filtered.size}")

        // 2. Separar por Tipo (Solo Completadas para el cuadre de caja real)
        // Si quieres incluir pendientes, quita la condición de estado.
        val activeTransactions = filtered.filter { it.estado == "Completada" }

        val purchases = activeTransactions.filter { it.tipoMovimiento.equals("Compra", true) }
        val sales = activeTransactions.filter { it.tipoMovimiento.equals("Venta", true) }

        // 3. Calcular Totales
        val totalCompraUSD = purchases.sumOf { it.montoDivisa }
        val totalCompraSoles = purchases.sumOf { it.montoSoles }
        val totalVentaUSD = sales.sumOf { it.montoDivisa }
        val totalVentaSoles = sales.sumOf { it.montoSoles }

        // Utilidad: (Ventas Soles - Compras Soles) -> Flujo de caja simple
        val utilidad = totalVentaSoles - totalCompraSoles

        // Tasa Promedio
        val tasas = activeTransactions.map { if (it.montoDivisa != 0.0) it.montoSoles / it.montoDivisa else 0.0 }
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

    // --- Helpers de Parseo Seguro ---

    // Extrae [Año, Mes] de "2025-11-21T10:00:00" o "2025-11-21"
    private fun parseDateParts(dateString: String): Pair<Int, Int>? {
        return try {
            // Nos quedamos solo con la parte de la fecha (antes de la T si existe)
            val cleanDate = dateString.split("T")[0]
            val parts = cleanDate.split("-")
            if (parts.size >= 2) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                Pair(year, month)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseYear(dateString: String): Int? {
        return parseDateParts(dateString)?.first
    }
}