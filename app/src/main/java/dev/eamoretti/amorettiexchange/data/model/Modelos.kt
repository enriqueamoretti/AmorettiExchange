package dev.eamoretti.amorettiexchange.data.model

import com.google.gson.annotations.SerializedName

// --- RESPUESTAS DE LA API ---

// Envoltorio genérico para respuestas GET (Listas, objetos)
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: String?
)

// Respuesta específica del Login
data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: Usuario?
)

// Respuesta para operaciones POST (Guardar/Crear)
data class PostResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("id") val idGenerado: Long?
)

// --- MODELOS DE DATOS ---

data class Usuario(
    @SerializedName("IdUsuario") val id: Int,
    @SerializedName("NombreCompleto") val nombreCompleto: String,
    @SerializedName("Email") val email: String
)

data class Cliente(
    @SerializedName("IdCliente") val id: Int,
    @SerializedName("RazonSocial") val razonSocial: String,
    @SerializedName("DocumentoIdentidad") val documento: String?,
    @SerializedName("TelefonoContacto") val telefono: String?,
    @SerializedName("Direccion") val direccion: String?
)

data class Transaccion(
    @SerializedName("IdTransaccion") val id: Long,
    @SerializedName("NombreCliente") val nombreCliente: String,
    @SerializedName("FechaOperacion") val fecha: String,
    @SerializedName("TipoMovimiento") val tipoMovimiento: String,
    @SerializedName("MonedaSimbolo") val moneda: String,
    @SerializedName("MontoDivisa") val montoDivisa: Double,
    @SerializedName("MontoLocal") val montoSoles: Double,
    @SerializedName("MetodoPago") val metodoPago: String,
    @SerializedName("Estado") val estado: String
)

data class ResumenMensual(
    @SerializedName("TotalCompraUSD") val totalCompraUSD: Double,
    @SerializedName("TotalCompraSoles") val totalCompraSoles: Double,
    @SerializedName("TotalVentaUSD") val totalVentaUSD: Double,
    @SerializedName("TotalVentaSoles") val totalVentaSoles: Double,
    @SerializedName("Utilidad") val utilidad: Double,
    @SerializedName("TasaPromedio") val tasaPromedio: Double
)

data class AnioDisponible(
    @SerializedName("Anio") val anio: Int
)

// --- CUERPOS DE ENVÍO (REQUESTS) ---

data class ClienteRequest(
    @SerializedName("idCliente") val idCliente: Int?,
    @SerializedName("razonSocial") val razonSocial: String,
    @SerializedName("dni") val dni: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("aux") val aux: String?,
    @SerializedName("cuenta") val cuenta: String?,
    @SerializedName("direccion") val direccion: String?
)

data class TransaccionRequest(
    @SerializedName("idCliente") val idCliente: Int,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("idTipoMov") val idTipoMov: Int,
    @SerializedName("idMoneda") val idMoneda: Int,
    @SerializedName("idTipoPago") val idTipoPago: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("tasa") val tasa: Double,
    @SerializedName("detalle") val detalle: String
)