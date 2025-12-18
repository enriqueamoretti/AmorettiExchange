package dev.eamoretti.amorettiexchange.data.network

import dev.eamoretti.amorettiexchange.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Clase simple para enviar usuario/pass
data class LoginRequest(val email: String, val password: String)

interface CambistaService {

    // 1. LOGIN
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 2. CLIENTES
    @GET("api/clients")
    suspend fun obtenerClientes(@Query("search") search: String = ""): Response<ApiResponse<List<Cliente>>>

    @POST("api/clients")
    suspend fun guardarCliente(@Body cliente: ClienteRequest): Response<PostResponse>

    // 3. TRANSACCIONES
    @GET("api/transactions")
    suspend fun obtenerTransacciones(
        @Query("search") search: String = "",
        @Query("type") type: Int? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): Response<ApiResponse<List<Transaccion>>>

    @POST("api/transactions")
    suspend fun guardarTransaccion(@Body transaccion: TransaccionRequest): Response<PostResponse>

    // 4. DASHBOARD
    @GET("api/dashboard")
    suspend fun obtenerDashboard(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<ApiResponse<ResumenMensual>>

    // 5. AGENTE IA (NUEVO)
    @POST("api/chat")
    suspend fun chatWithAgent(@Body request: ChatRequest): Response<ChatResponse>
}