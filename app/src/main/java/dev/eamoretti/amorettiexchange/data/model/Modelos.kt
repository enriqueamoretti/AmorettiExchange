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