package dev.eamoretti.amorettiexchange.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
// Ya no necesitamos importar @Query porque no enviaremos la llave

data class ApiRequest(
    @SerializedName("operation") val operation: String,
    @SerializedName("payload") val payload: Map<String, Any?>
)

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("error") val error: String?
)

interface CambistaService {
    // Endpoint limpio: Ya no pide 'functionKey'
    @POST("api/GestionarOperaciones")
    suspend fun ejecutarOperacion(
        @Body request: ApiRequest
    ): Response<ApiResponse<Any>>
}