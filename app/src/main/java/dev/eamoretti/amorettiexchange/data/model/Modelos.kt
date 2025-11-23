package dev.eamoretti.amorettiexchange.data.model

import com.google.gson.annotations.SerializedName

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

// NUEVO: Modelo de Usuario
data class Usuario(
    @SerializedName("IdUsuario") val id: Int,
    @SerializedName("NombreCompleto") val nombreCompleto: String,
    @SerializedName("Email") val email: String
)