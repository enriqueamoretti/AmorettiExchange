package dev.eamoretti.amorettiexchange.presentation.clients.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eamoretti.amorettiexchange.presentation.clients.Client

@Composable
fun ClientListItem(client: Client) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = "Cliente",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF092B5A)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(client.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = "RUC", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(client.ruc, color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = "Teléfono", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(client.phone, color = Color.Gray, fontSize = 14.sp)
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Ver detalles",
                tint = Color.Gray
            )
        }
    }
}