package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterTransactionViewModel : ViewModel() {

    // --- ESTADOS DEL FORMULARIO ---
    var selectedClient by mutableStateOf<Cliente?>(null)
    // Fecha por defecto: Hoy
    var date by mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

    var movementType by mutableStateOf("Compra") // UI: Compra/Venta
    var currency by mutableStateOf("Dólares")    // UI: Dólares/Euros/Soles
    var amount by mutableStateOf("")
    var exchangeRate by mutableStateOf("3.375")
    var paymentType by mutableStateOf("Efectivo")
    var detail by mutableStateOf("")
    var status by mutableStateOf("Completada")

    // Resultado visual (Total en Soles)
    var totalInSoles by mutableStateOf("S/ 0.00")

    // --- ESTADOS DE CONTROL ---
    var clientsList by mutableStateOf<List<Cliente>>(emptyList()) // Lista completa
    var filteredClients by mutableStateOf<List<Cliente>>(emptyList()) // Lista filtrada por texto
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    private val service = ApiClient.retrofit.create(CambistaService::class.java)

    init {
        loadClientsFromCache()
    }

    // 1. Cargar clientes desde el Repositorio (Cero consumo si ya están en memoria)
    private fun loadClientsFromCache() {
        viewModelScope.launch {
            try {
                val clients = DataRepository.obtenerClientes(forzarRecarga = false)
                clientsList = clients
                filteredClients = clients // Al inicio mostramos todos
            } catch (e: Exception) {
                errorMessage = "Error al cargar clientes: ${e.message}"
            }
        }
    }

    // 2. Filtrar clientes mientras escribes en el Dropdown
    fun filterClients(query: String) {
        filteredClients = if (query.isEmpty()) {
            clientsList
        } else {
            clientsList.filter {
                it.razonSocial.contains(query, ignoreCase = true) ||
                        (it.documento?.contains(query) == true)
            }
        }
    }

    // 3. Calcular Total en Soles automáticamente
    fun calculateTotal() {
        val amountVal = amount.toDoubleOrNull() ?: 0.0
        val rateVal = exchangeRate.toDoubleOrNull() ?: 0.0

        // Lógica simple: Monto * Tasa
        val total = amountVal * rateVal
        totalInSoles = "S/ ${DecimalFormat("#,##0.00").format(total)}"
    }

    // 4. Guardar Transacción
    fun saveTransaction() {
        if (selectedClient == null || amount.isBlank() || exchangeRate.isBlank()) {
            errorMessage = "Complete los campos obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Mapeo de Valores de UI a IDs de Base de Datos (Según tus tablas)
                val idMovimiento = if (movementType == "Compra") 1 else 2

                val idMoneda = when (currency) {
                    "Dólares" -> 1
                    "Euros" -> 2
                    "Soles" -> 3
                    else -> 1
                }

                val idPago = when (paymentType) {
                    "Efectivo" -> 1
                    "Transferencia" -> 2
                    "Yape/Plin" -> 3
                    else -> 4 // Cheque u Otro
                }

                val idEstado = when (status) {
                    "Completada" -> 1
                    "Anulada" -> 2
                    "Pendiente" -> 3
                    else -> 1
                }

                val payload = mapOf(
                    "IdCliente" to selectedClient!!.id,
                    "IdUsuario" to 1, // Por ahora Admin (luego lo tomaremos del login)
                    "FechaOperacion" to "$date ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}", // Fecha + Hora actual
                    "IdTipoMovimiento" to idMovimiento,
                    "IdMoneda" to idMoneda,
                    "IdTipoPago" to idPago,
                    "MontoDivisa" to (amount.toDoubleOrNull() ?: 0.0),
                    "TasaCambio" to (exchangeRate.toDoubleOrNull() ?: 0.0),
                    "Detalle" to detail,
                    "IdEstado" to idEstado // Opcional, el SP lo pone en 1 por defecto
                )

                val request = ApiRequest("SP_RegistrarTransaccion", payload)
                val response = service.ejecutarOperacion(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    // ¡Éxito! Invalidamos caché para que al volver se vean los cambios
                    DataRepository.invalidarCacheGlobal()
                    isSuccess = true
                } else {
                    errorMessage = response.body()?.error ?: "Error al guardar"
                }

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
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