package dev.eamoretti.amorettiexchange.presentation.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionListItem(transaction: Transaccion) {
    // 1. Determinar si es Venta o Compra (Azure devuelve "Venta" o "Compra")
    val isSale = transaction.tipoMovimiento.equals("Venta", ignoreCase = true)

    // 2. Configurar colores (Verde para venta, Azul para compra - O Rojo si prefieres como tenías antes)
    // Usaré tus colores: Venta (Verde), Compra (Azul/Rojo)
    val mainColor = if (isSale) Color(0xFF2E7D32) else Color(0xFF1565C0) // Verde vs Azul Fuerte
    val bgColor = if (isSale) Color(0xFFE8F5E9) else Color(0xFFE3F2FD) // Fondos suaves
    val icon = if (isSale) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    // 3. Formatear dinero
    val formatUSD = NumberFormat.getCurrencyInstance(Locale.US)
    val formatPEN = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // --- Encabezado ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono con fondo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(bgColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = mainColor
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Etiqueta Tipo
                    Text(
                        text = transaction.tipoMovimiento.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = mainColor,
                        fontSize = 14.sp
                    )
                    // Fecha
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        // Tomamos solo la fecha YYYY-MM-DD
                        Text(transaction.fecha.take(10), color = Color.Gray, fontSize = 13.sp)
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // --- Cliente ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = transaction.nombreCliente,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937),
                    maxLines = 1
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))

            // --- Montos ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monto Divisa", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = formatUSD.format(transaction.montoDivisa),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Soles", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = formatPEN.format(transaction.montoSoles),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- Footer (Chips) ---
            Row {
                Chip(label = transaction.metodoPago)
                Spacer(Modifier.width(8.dp))
                Chip(
                    label = transaction.estado,
                    containerColor = Color(0xFFDCFCE7), // Verde claro éxito
                    labelColor = Color(0xFF166534)
                )
            }
        }
    }
}

// Tu componente Chip auxiliar (lo mantengo porque es útil)
@Composable
fun Chip(label: String, containerColor: Color = Color(0xFFF3F4F6), labelColor: Color = Color(0xFF4B5563)) {
    Box(
        modifier = Modifier
            .background(containerColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = labelColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}