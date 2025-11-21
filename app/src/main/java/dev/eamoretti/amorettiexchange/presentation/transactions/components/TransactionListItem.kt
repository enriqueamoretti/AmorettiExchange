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

enum class TransactionType {
    PURCHASE, SALE
}

data class Transaction(
    val type: TransactionType,
    val date: String,
    val clientName: String,
    val amount: String,
    val totalSoles: String,
    val paymentMethod: String,
    val status: String
)

@Composable
fun TransactionListItem(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.SALE) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = "Tipo de transacción",
                    modifier = Modifier.size(40.dp),
                    tint = if (transaction.type == TransactionType.SALE) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (transaction.type == TransactionType.SALE) "Venta" else "Compra",
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.type == TransactionType.SALE) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha", modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(transaction.date, color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Ver detalles",
                    tint = Color.Gray
                )
            }
            Spacer(Modifier.height(12.dp))
            // Client Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Cliente", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(transaction.clientName, fontSize = 14.sp)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            // Amounts Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monto", color = Color.Gray, fontSize = 12.sp)
                    Text(transaction.amount, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Soles", color = Color.Gray, fontSize = 12.sp)
                    Text(transaction.totalSoles, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            // Footer Row
            Row {
                Chip(label = transaction.paymentMethod)
                Spacer(Modifier.width(8.dp))
                Chip(label = transaction.status, containerColor = Color(0xFFE8F5E9), labelColor = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
fun Chip(label: String, containerColor: Color = Color(0xFFF5F5F5), labelColor: Color = Color.Black) {
    Box(
        modifier = Modifier
            .background(containerColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = labelColor, fontSize = 12.sp)
    }
}