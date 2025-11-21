package dev.eamoretti.amorettiexchange.presentation.monthlybalancing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components.Movement
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components.MovementListItem
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBalancingScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuadre Mensual") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF092B5A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                FilterSection()
            }
            item {
                SummarySection()
            }
            item {
                MovementSection(
                    title = "Movimientos de Compra",
                    movements = listOf(
                        Movement("17 nov. 2025", "$ 12,000.00", "S/ 40,260.00"),
                        Movement("16 nov. 2025", "$ 18,356.00", "S/ 61,309.04"),
                        Movement("14 nov. 2025", "$ 5,885.00", "S/ 20,193.93"),
                        Movement("10 nov. 2025", "$ 29,285.60", "S/ 98,978.18")
                    )
                )
            }
            item {
                MovementSection(
                    title = "Movimientos de Venta",
                    movements = listOf(
                        Movement("17 nov. 2025", "$ 18,228.60", "S/ 61,659.96"),
                        Movement("16 nov. 2025", "$ 2,105.00", "S/ 7,093.85"),
                        Movement("12 nov. 2025", "$ 13,320.00", "S/ 44,895.44")
                    )
                )
            }
        }
    }
}

@Composable
fun FilterSection() {
    var selectedMonth by remember { mutableStateOf("Noviembre") }
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF092B5A))
            .padding(16.dp)
    ) {
        Text("Año", color = Color.White.copy(alpha = 0.8f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("2025", color = Color.White, fontSize = 18.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar año", tint = Color.White)
        }
        Spacer(Modifier.height(16.dp))
        Text("Mes", color = Color.White.copy(alpha = 0.8f))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(months) { month ->
                val isSelected = month == selectedMonth
                Button(
                    onClick = { selectedMonth = month },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFFFFFF) else Color(0x33FFFFFF),
                        contentColor = if (isSelected) Color(0xFF092B5A) else Color.White
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(month)
                }
            }
        }
    }
}

@Composable
fun SummarySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Resumen General", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingDown, "Total Compra USD", "$ 65,526.60", Color(0xFFC62828))
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingDown, "Total Compra Soles", "S/ 220,741.15", Color(0xFFC62828))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingUp, "Total Venta USD", "$ 33,653.60", Color(0xFF2E7D32))
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingUp, "Total Venta Soles", "S/ 113,649.25", Color(0xFF2E7D32))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.AttachMoney, "Utilidad", "S/ -107,091.90", Color(0xFFF9A825))
                SummaryCard(Modifier.weight(1f), Icons.Default.CompareArrows, "Tasa de Conversión", "3.370", Color(0xFF1565C0))
            }
        }
    }
}

@Composable
fun MovementSection(title: String, movements: List<Movement>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            movements.forEach { movement ->
                MovementListItem(movement = movement)
            }
        }
    }
}