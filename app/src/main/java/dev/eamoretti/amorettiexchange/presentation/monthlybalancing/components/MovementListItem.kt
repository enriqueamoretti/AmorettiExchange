package dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
fun MovementListItem(movement: Transaccion) {
    val formatUSD = NumberFormat.getCurrencyInstance(Locale.US)
    val formatPEN = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Fecha", tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                // Formatear fecha simple
                Text(movement.fecha.take(10), fontSize = 15.sp, color = Color(0xFF374151))
            }

            Spacer(Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatUSD.format(movement.montoDivisa),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = formatPEN.format(movement.montoSoles),
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}