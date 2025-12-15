package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.repository.DataRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterTransactionViewModel : ViewModel() {

    // --- ESTADOS DEL FORMULARIO ---
    var selectedClient by mutableStateOf<Cliente?>(null)
    var date by mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

    var movementType by mutableStateOf("Compra")
    var currency by mutableStateOf("Dólares")
    var amount by mutableStateOf("")
    var exchangeRate by mutableStateOf("3.375")
    var paymentType by mutableStateOf("Efectivo")
    var detail by mutableStateOf("")
    var status by mutableStateOf("Completada")

    // Resultado visual
    var totalInSoles by mutableStateOf("S/ 0.00")

    // --- ESTADOS DE CONTROL ---
    var clientsList by mutableStateOf<List<Cliente>>(emptyList())
    var filteredClients by mutableStateOf<List<Cliente>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    init {
        loadClientsFromCache()
    }

    private fun loadClientsFromCache() {
        viewModelScope.launch {
            try {
                val clients = DataRepository.obtenerClientes(forzarRecarga = false)
                clientsList = clients
                filteredClients = clients
            } catch (e: Exception) {
                errorMessage = "Error al cargar clientes: ${e.message}"
            }
        }
    }

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

    fun calculateTotal() {
        val amountVal = amount.toDoubleOrNull() ?: 0.0
        val rateVal = exchangeRate.toDoubleOrNull() ?: 0.0
        val total = amountVal * rateVal
        totalInSoles = "S/ ${DecimalFormat("#,##0.00").format(total)}"
    }

    fun saveTransaction() {
        if (selectedClient == null || amount.isBlank() || exchangeRate.isBlank()) {
            errorMessage = "Complete los campos obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Mapeos (UI -> IDs)
                val idMovimiento = if (movementType == "Compra") 1 else 2
                val idMoneda = when (currency) { "Dólares" -> 1; "Euros" -> 2; "Soles" -> 3; else -> 1 }
                val idPago = when (paymentType) { "Efectivo" -> 1; "Transferencia" -> 2; "Yape/Plin" -> 3; else -> 4 }

                // Fecha + Hora
                val fechaCompleta = "$date ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"

                // Llamada limpia
                val exito = DataRepository.guardarTransaccion(
                    idCliente = selectedClient!!.id,
                    fecha = fechaCompleta,
                    idMov = idMovimiento,
                    idMoneda = idMoneda,
                    idPago = idPago,
                    monto = amount.toDoubleOrNull() ?: 0.0,
                    tasa = exchangeRate.toDoubleOrNull() ?: 0.0,
                    detalle = detail
                )

                if (exito) {
                    isSuccess = true
                }

            } catch (e: Exception) {
                errorMessage = e.message ?: "Error al guardar"
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