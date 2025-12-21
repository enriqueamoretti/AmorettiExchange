package dev.eamoretti.amorettiexchange.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.eamoretti.amorettiexchange.data.model.*
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.CambistaService
import dev.eamoretti.amorettiexchange.data.network.LoginRequest

object DataRepository {

    private lateinit var service: CambistaService
    private val gson = Gson()

    private const val PREFS_NAME = "CambistaCache"
    private const val KEY_CLIENTS = "cached_clients"
    private const val KEY_USER = "current_user"
    private const val KEY_TOKEN = "auth_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Inicializamos con el contexto para que funcione el Interceptor del Token
        service = ApiClient.getClient(context).create(CambistaService::class.java)
    }

    // --- USUARIO Y SESIÓN ---

    fun obtenerUsuarioSesion(): Usuario? {
        if (!::prefs.isInitialized) return null
        val jsonUser = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(jsonUser, Usuario::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun login(email: String, pass: String): Usuario? {
        val response = service.login(LoginRequest(email, pass))

        if (response.isSuccessful && response.body()?.success == true) {
            val body = response.body()!!

            // Guardar Token y Usuario
            prefs.edit()
                .putString(KEY_TOKEN, body.token)
                .putString(KEY_USER, gson.toJson(body.user))
                .apply()

            return body.user
        } else {
            throw Exception("Credenciales incorrectas o error de servidor")
        }
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
        invalidarCacheGlobal()
    }

    // --- CLIENTES ---

    private var memoryClients: List<Cliente>? = null

    suspend fun obtenerClientes(forzarRecarga: Boolean = false): List<Cliente> {
        if (!forzarRecarga && memoryClients != null) return memoryClients!!

        // Cache local
        if (!forzarRecarga) {
            val jsonLocal = prefs.getString(KEY_CLIENTS, null)
            if (jsonLocal != null) {
                val type = object : TypeToken<List<Cliente>>() {}.type
                memoryClients = gson.fromJson(jsonLocal, type)
                return memoryClients!!
            }
        }

        // Llamada API
        val response = service.obtenerClientes()
        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data ?: emptyList()
            prefs.edit().putString(KEY_CLIENTS, gson.toJson(data)).apply()
            memoryClients = data
            return data
        } else {
            throw Exception("Error al cargar clientes")
        }
    }

    // Método auxiliar para obtener cliente desde memoria sin llamar API (útil para Detalle)
    fun obtenerClienteDesdeMemoria(id: Int): Cliente? {
        return memoryClients?.find { it.id == id }
    }

    suspend fun guardarCliente(razonSocial: String, ruc: String?, tel: String?, aux: String?, cta: String?, dir: String?): Boolean {
        // ID es null porque es nuevo
        val request = ClienteRequest(null, razonSocial, ruc, tel, aux, cta, dir)
        val response = service.guardarCliente(request)

        if (response.isSuccessful && response.body()?.success == true) {
            invalidarCacheGlobal() // Para recargar lista al volver
            return true
        } else {
            throw Exception(response.body()?.message ?: "Error al guardar")
        }
    }

    // --- NUEVO: EDITAR CLIENTE ---
    suspend fun editarCliente(id: Int, razonSocial: String, ruc: String?, tel: String?, aux: String?, cta: String?, dir: String?): Boolean {
        val request = ClienteRequest(id, razonSocial, ruc, tel, aux, cta, dir)
        val response = service.editarCliente(id, request)

        if (response.isSuccessful && response.body()?.success == true) {
            invalidarCacheGlobal()
            return true
        } else {
            throw Exception(response.body()?.message ?: "Error al editar")
        }
    }

    // --- NUEVO: ELIMINAR CLIENTE ---
    suspend fun eliminarCliente(id: Int): Boolean {
        val response = service.eliminarCliente(id)

        if (response.isSuccessful && response.body()?.success == true) {
            invalidarCacheGlobal()
            return true
        } else {
            // Aquí capturamos el mensaje del backend (ej: "No se puede eliminar, tiene transacciones")
            throw Exception(response.body()?.message ?: "Error al eliminar. Posiblemente tenga transacciones asociadas.")
        }
    }

    // --- TRANSACCIONES ---

    private var memoryTransactions: List<Transaccion>? = null

    suspend fun obtenerTransacciones(forzarRecarga: Boolean = false): List<Transaccion> {
        if (!forzarRecarga && memoryTransactions != null) return memoryTransactions!!

        val response = service.obtenerTransacciones()
        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data ?: emptyList()
            memoryTransactions = data
            return data
        } else {
            throw Exception("Error al cargar transacciones")
        }
    }

    // --- NUEVO: HISTORIAL POR CLIENTE ---
    suspend fun obtenerHistorialCliente(idCliente: Int): List<Transaccion> {
        val response = service.obtenerHistorialCliente(idCliente)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data ?: emptyList()
        } else {
            throw Exception("Error al obtener historial del cliente")
        }
    }

    suspend fun guardarTransaccion(idCliente: Int, fecha: String, idMov: Int, idMoneda: Int, idPago: Int, monto: Double, tasa: Double, detalle: String): Boolean {
        val request = TransaccionRequest(idCliente, fecha, idMov, idMoneda, idPago, monto, tasa, detalle)
        val response = service.guardarTransaccion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            invalidarCacheGlobal()
            return true
        } else {
            throw Exception(response.body()?.message ?: "Error al guardar")
        }
    }

    // --- DASHBOARD ---
    suspend fun obtenerDashboard(year: Int, month: Int): ResumenMensual? {
        val response = service.obtenerDashboard(year, month)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        }
        return null
    }

    fun invalidarCacheGlobal() {
        memoryClients = null
        memoryTransactions = null
        if (::prefs.isInitialized) {
            prefs.edit().remove(KEY_CLIENTS).apply()
        }
    }
}