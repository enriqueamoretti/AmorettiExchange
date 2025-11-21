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