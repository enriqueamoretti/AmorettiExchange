package dev.eamoretti.amorettiexchange.data.repository

import android.util.Log
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Singleton: Existe una única instancia para toda la App mientras esté abierta
object DataRepository {

    private val service = ApiClient.retrofit.create(CambistaService::class.java)
    private val gson = Gson()

    // --- MEMORIA CACHÉ (Aquí se guardan los datos para no pedirlos de nuevo) ---
    private var cachedClients: List<Cliente>? = null
    private var cachedTransactions: List<Transaccion>? = null

    // Función Inteligente para Clientes
    suspend fun obtenerClientes(forzarRecarga: Boolean = false): List<Cliente> {
        // 1. Si ya tenemos datos y no forzamos recarga, devolver caché (CERO CONSUMO)
        if (!forzarRecarga && cachedClients != null) {
            Log.d("Repository", "🚀 Devolviendo Clientes desde CACHÉ (Sin internet)")
            return cachedClients!!
        }

        // 2. Si no hay datos, ir a la API (CONSUMO REAL)
        Log.d("Repository", "🌐 Conectando a API Azure para Clientes...")
        val request = ApiRequest(operation = "SP_ListarClientes", payload = mapOf("Busqueda" to ""))
        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val dataJson = gson.toJson(response.body()!!.data)
            val tipoLista = object : TypeToken<List<Cliente>>() {}.type
            val lista: List<Cliente> = gson.fromJson(dataJson, tipoLista)

            // Guardar en caché para la próxima vez
            cachedClients = lista
            return lista
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener clientes")
        }
    }

    // Función Inteligente para Transacciones
    suspend fun obtenerTransacciones(forzarRecarga: Boolean = false): List<Transaccion> {
        if (!forzarRecarga && cachedTransactions != null) {
            Log.d("Repository", "🚀 Devolviendo Transacciones desde CACHÉ (Sin internet)")
            return cachedTransactions!!
        }

        Log.d("Repository", "🌐 Conectando a API Azure para Transacciones...")
        // Pedimos TODAS las transacciones (filtros nulos) para guardar la lista maestra
        val request = ApiRequest(
            operation = "SP_ListarTransacciones",
            payload = mapOf("Busqueda" to "", "IdTipoMovimiento" to null)
        )
        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val dataJson = gson.toJson(response.body()!!.data)
            val tipoLista = object : TypeToken<List<Transaccion>>() {}.type
            val lista: List<Transaccion> = gson.fromJson(dataJson, tipoLista)

            cachedTransactions = lista
            return lista
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener transacciones")
        }
    }

    // Función para limpiar caché (útil cuando registras algo nuevo y quieres ver los cambios)
    fun limpiarCache() {
        cachedClients = null
        cachedTransactions = null
    }
}