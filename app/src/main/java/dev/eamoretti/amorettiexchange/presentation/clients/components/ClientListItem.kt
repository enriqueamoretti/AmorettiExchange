package dev.eamoretti.amorettiexchange.presentation.clients.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Usamos directamente el modelo de datos de la API
import dev.eamoretti.amorettiexchange.data.model.Cliente

@Composable
fun ClientListItem(client: Cliente) {
    Card(
        shape = RoundedCornerShape(12.dp), // Bordes suaves
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp), // Espacio interno cómodo
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LÓGICA VISUAL: ¿Empresa o Persona?
            // Si contiene siglas comerciales, asumimos empresa
            val isCompany = client.razonSocial.uppercase().let {
                it.contains("SAC") || it.contains("S.A") || it.contains("E.I.R.L") || it.contains("SRL")
            }

            // Avatar con fondo suave
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF0F4F8), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompany) Icons.Default.Business else Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF092B5A), // Tu azul corporativo
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Columna de Datos
            Column(modifier = Modifier.weight(1f)) {
                // 1. NOMBRE (Siempre visible)
                Text(
                    text = client.razonSocial,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1F2937),
                    lineHeight = 20.sp
                )

                // 2. DOCUMENTO (Solo si existe y no está vacío)
                if (!client.documento.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Badge, // O Business si Badge no está disponible
                            contentDescription = "RUC/DNI",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF6B7280) // Gris suave
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = client.documento,
                            color = Color(0xFF6B7280),
                            fontSize = 13.sp
                        )
                    }
                }

                // 3. TELÉFONO (Solo si existe y no está vacío)
                if (!client.telefono.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Teléfono",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = client.telefono,
                            color = Color(0xFF6B7280),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Flechita discreta al final
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFE5E7EB), // Muy sutil
                modifier = Modifier.size(16.dp)
            )
        }
    }
}