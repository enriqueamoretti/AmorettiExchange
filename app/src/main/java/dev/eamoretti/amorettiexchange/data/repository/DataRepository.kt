package dev.eamoretti.amorettiexchange.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.eamoretti.amorettiexchange.data.model.Cliente
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.data.model.Usuario // Asegúrate de importar Usuario
import dev.eamoretti.amorettiexchange.data.network.ApiClient
import dev.eamoretti.amorettiexchange.data.network.ApiRequest
import dev.eamoretti.amorettiexchange.data.network.CambistaService

object DataRepository {

    private val service = ApiClient.retrofit.create(CambistaService::class.java)
    private val gson = Gson()
    private const val PREFS_NAME = "CambistaCache"
    private const val KEY_CLIENTS = "cached_clients"
    private const val KEY_TRANSACTIONS = "cached_transactions"
    private const val KEY_USER = "current_user"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // --- ESTA ES LA FUNCIÓN QUE TE FALTA O NO ES VISIBLE ---
    fun obtenerUsuarioSesion(): Usuario? {
        // Verifica que 'prefs' esté inicializado. Si llamas a esto antes de init(), podría fallar en runtime,
        // pero el error de compilación es porque no encuentra la función.
        if (!::prefs.isInitialized) return null

        val jsonUser = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(jsonUser, Usuario::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun login(email: String, pass: String): Usuario? {
        val request = ApiRequest(
            operation = "SP_LoginUsuario",
            payload = mapOf("Email" to email, "PasswordHash" to pass)
        )

        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val dataJson = gson.toJson(response.body()!!.data)
            val type = object : TypeToken<List<Usuario>>() {}.type
            val usuarios: List<Usuario> = gson.fromJson(dataJson, type)

            return if (usuarios.isNotEmpty()) {
                val usuarioEncontrado = usuarios[0]
                // Guardar sesión en disco
                prefs.edit().putString(KEY_USER, gson.toJson(usuarioEncontrado)).apply()
                usuarioEncontrado
            } else {
                null
            }
        } else {
            throw Exception(response.body()?.error ?: "Error en el servicio de login")
        }
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
        invalidarCacheGlobal()
    }

    // ... (El resto de tus funciones obtenerClientes, obtenerTransacciones, etc. siguen igual) ...
    private var memoryClients: List<Cliente>? = null
    private var memoryTransactions: List<Transaccion>? = null

    suspend fun obtenerClientes(forzarRecarga: Boolean = false): List<Cliente> {
        if (!forzarRecarga && memoryClients != null) return memoryClients!!

        if (!forzarRecarga) {
            val jsonLocal = prefs.getString(KEY_CLIENTS, null)
            if (jsonLocal != null) {
                val type = object : TypeToken<List<Cliente>>() {}.type
                memoryClients = gson.fromJson(jsonLocal, type)
                return memoryClients!!
            }
        }

        val request = ApiRequest(operation = "SP_ListarClientes", payload = mapOf("Busqueda" to ""))
        val response = service.ejecutarOperacion(request)

        if (response.isSuccessful && response.body()?.success == true) {
            val data = response.body()!!.data
            val jsonString = gson.toJson(data)
            prefs.edit().putString(KEY_CLIENTS, jsonString).apply()

            val type = object : TypeToken<List<Cliente>>() {}.type
            memoryClients = gson.fromJson(jsonString, type)
            return memoryClients!!
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener clientes")
        }
    }

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
            memoryTransactions = gson.fromJson(jsonString, type)
            return memoryTransactions!!
        } else {
            throw Exception(response.body()?.error ?: "Error al obtener transacciones")
        }
    }

    fun invalidarCacheGlobal() {
        memoryClients = null
        memoryTransactions = null
    }
}