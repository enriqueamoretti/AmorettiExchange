package dev.eamoretti.amorettiexchange.data.model

import com.google.gson.annotations.SerializedName

data class Cliente(
    @SerializedName("IdCliente") val id: Int,
    @SerializedName("RazonSocial") val razonSocial: String,
    @SerializedName("DocumentoIdentidad") val documento: String?,
    @SerializedName("TelefonoContacto") val telefono: String?,
    @SerializedName("Direccion") val direccion: String?
)

// Aquí puedes agregar Transaccion y ResumenMensual luego

// AGREGAR ESTO: Modelo para las transacciones
data class Transaccion(
    @SerializedName("IdTransaccion") val id: Long,
    @SerializedName("NombreCliente") val nombreCliente: String,
    @SerializedName("FechaOperacion") val fecha: String, // Viene como "2025-11-17T00:00:00"
    @SerializedName("TipoMovimiento") val tipoMovimiento: String, // "Compra" o "Venta"
    @SerializedName("MonedaSimbolo") val moneda: String,
    @SerializedName("MontoDivisa") val montoDivisa: Double,
    @SerializedName("MontoLocal") val montoSoles: Double,
    @SerializedName("MetodoPago") val metodoPago: String,
    @SerializedName("Estado") val estado: String
)

// Nuevo modelo para el Resumen (Tarjetas de colores)
data class ResumenMensual(
    @SerializedName("TotalCompraUSD") val totalCompraUSD: Double,
    @SerializedName("TotalCompraSoles") val totalCompraSoles: Double,
    @SerializedName("TotalVentaUSD") val totalVentaUSD: Double,
    @SerializedName("TotalVentaSoles") val totalVentaSoles: Double,
    @SerializedName("Utilidad") val utilidad: Double,
    @SerializedName("TasaPromedio") val tasaPromedio: Double
)

// Nuevo modelo para la lista de Años disponibles
data class AnioDisponible(
    @SerializedName("Anio") val anio: Int
)