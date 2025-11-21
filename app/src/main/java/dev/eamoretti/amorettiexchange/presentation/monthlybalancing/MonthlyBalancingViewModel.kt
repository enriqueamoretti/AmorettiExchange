package dev.eamoretti.amorettiexchange.presentation.monthlybalancing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.ResumenMensual
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.repository.DataRepository // Usamos el Repo inteligente
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
    val selectedMonthIndex: Int = Calendar.getInstance().get(Calendar.MONTH), // 0=Enero

    val summary: ResumenMensual? = null,
    val purchaseMovements: List<Transaccion> = emptyList(),
    val saleMovements: List<Transaccion> = emptyList(),

    val error: String? = null
)

class MonthlyBalancingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyUiState())
    val uiState: StateFlow<MonthlyUiState> = _uiState.asStateFlow()

    // Aquí guardaremos TODO el historial para no volver a pedirlo
    private var allTransactions: List<Transaccion> = emptyList()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Pedir TODAS las transacciones al Repositorio (Usa Caché si ya existen)
                // Esto consume API solo la primera vez que abres la app
                allTransactions = DataRepository.obtenerTransacciones()

                // 2. Calcular qué años tienen datos (Lógica local)
                val availableYears = allTransactions
                    .map { it.fecha.take(4).toInt() } // Extraer "2025" de "2025-11-..."
                    .distinct()
                    .sortedDescending()

                // Actualizar años si encontramos datos
                if (availableYears.isNotEmpty()) {
                    _uiState.update { it.copy(years = availableYears, selectedYear = availableYears.first()) }
                }

                // 3. Calcular el reporte inicial
                calculateLocalReport()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onYearSelected(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        calculateLocalReport() // Cálculo instantáneo en RAM
    }

    fun onMonthSelected(monthIndex: Int) {
        _uiState.update { it.copy(selectedMonthIndex = monthIndex) }
        calculateLocalReport() // Cálculo instantáneo en RAM
    }

    // ESTA ES LA MAGIA: Filtra y suma en tu celular (Costo $0)
    private fun calculateLocalReport() {
        val year = _uiState.value.selectedYear
        val month = _uiState.value.selectedMonthIndex + 1 // Ajuste 0-11 a 1-12

        // 1. Filtrar lista maestra
        val filtered = allTransactions.filter { t ->
            val tYear = t.fecha.take(4).toInt()
            val tMonth = t.fecha.substring(5, 7).toInt()
            tYear == year && tMonth == month
        }

        // 2. Separar Compras y Ventas
        val purchases = filtered.filter { it.tipoMovimiento.equals("Compra", true) }
        val sales = filtered.filter { it.tipoMovimiento.equals("Venta", true) }

        // 3. Calcular Totales Matemáticamente
        val totalCompraUSD = purchases.sumOf { it.montoDivisa }
        val totalCompraSoles = purchases.sumOf { it.montoSoles }
        val totalVentaUSD = sales.sumOf { it.montoDivisa }
        val totalVentaSoles = sales.sumOf { it.montoSoles }
        val utilidad = totalVentaSoles - totalCompraSoles

        // Calcular Tasa Promedio (Evitar división por cero)
        // Una fórmula simple: Total Soles Movidos / Total Dólares Movidos (o promedio simple de tasas)
        // Usaremos promedio simple de las tasas registradas en el mes
        val tasas = filtered.map { it.montoSoles / it.montoDivisa } // Calculamos tasa implícita de cada op
        val tasaPromedio = if (tasas.isNotEmpty()) tasas.average() else 0.0

        // 4. Crear objeto Resumen
        val resumen = ResumenMensual(
            totalCompraUSD = totalCompraUSD,
            totalCompraSoles = totalCompraSoles,
            totalVentaUSD = totalVentaUSD,
            totalVentaSoles = totalVentaSoles,
            utilidad = utilidad,
            tasaPromedio = tasaPromedio
        )

        // 5. Actualizar UI (Instantáneo)
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