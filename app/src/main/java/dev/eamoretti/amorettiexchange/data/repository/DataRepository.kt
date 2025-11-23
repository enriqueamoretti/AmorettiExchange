package dev.eamoretti.amorettiexchange.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService

object DataRepository {

    private val service = ApiClient.retrofit.create(CambistaService::class.java)
    private val gson = Gson()
    private const val PREFS_NAME = "CambistaCache"
    private const val KEY_CLIENTS = "cached_clients"
    private const val KEY_TRANSACTIONS = "cached_transactions"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // MEMORIA RAM (Acceso instantáneo)
    private var memoryClients: List<Cliente>? = null
    private var memoryTransactions: List<Transaccion>? = null

    // --- CLIENTES ---
    suspend fun obtenerClientes(forzarRecarga: Boolean = false): List<Cliente> {
        // 1. Memoria RAM (Lo más rápido)
        if (!forzarRecarga && memoryClients != null) return memoryClients!!

        // 2. Disco (Si no hay RAM y no forzamos)
        if (!forzarRecarga) {
            val jsonLocal = prefs.getString(KEY_CLIENTS, null)
            if (jsonLocal != null) {
                val type = object : TypeToken<List<Cliente>>() {}.type
                memoryClients = gson.fromJson(jsonLocal, type)
                return memoryClients!!
            }
        }

        // 3. Nube (Si forzamos o no hay nada local)
        val request = ApiRequest(operation = "SP_ListarClientes", payload = mapOf("Busqueda" to ""))
        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data
            val jsonString = gson.toJson(data)

            prefs.edit().putString(KEY_CLIENTS, jsonString).apply()

            val type = object : TypeToken<List<Cliente>>() {}.type
            val lista: List<Cliente> = gson.fromJson(jsonString, type)
            memoryClients = lista
            return lista
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener clientes")
        }
    }

    // --- TRANSACCIONES ---
    suspend fun obtenerTransacciones(forzarRecarga: Boolean = false): List<Transaccion> {
        if (!forzarRecarga && memoryTransactions != null) return memoryTransactions!!

        if (!forzarRecarga) {
            val jsonLocal = prefs.getString(KEY_TRANSACTIONS, null)
            if (jsonLocal != null) {
                val type = object : TypeToken<List<Transaccion>>() {}.type
                memoryTransactions = gson.fromJson(jsonLocal, type)
                return memoryTransactions!!
            }
        }

        val request = ApiRequest(
            operation = "SP_ListarTransacciones",
            payload = mapOf("Busqueda" to "", "IdTipoMovimiento" to null)
        )
        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data
            val jsonString = gson.toJson(data)

            prefs.edit().putString(KEY_TRANSACTIONS, jsonString).apply()

            val type = object : TypeToken<List<Transaccion>>() {}.type
            val lista: List<Transaccion> = gson.fromJson(jsonString, type)
            memoryTransactions = lista
            return lista
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener transacciones")
        }
    }

    // Función Mágica: Borra la memoria RAM para obligar a las otras pantallas a recargar
    fun invalidarCacheGlobal() {
        memoryClients = null
        memoryTransactions = null
        // Nota: No borramos el disco para que sigan teniendo algo que mostrar mientras cargan.
        // Al poner la memoria en null, la próxima consulta con 'force=true' irá a la nube.
    }
}