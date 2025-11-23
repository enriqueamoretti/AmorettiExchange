package dev.eamoretti.amorettiexchange.presentation.monthlybalancing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.eamoretti.amorettiexchange.data.model.Transaccion
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components.MovementListItem
import dev.eamoretti.amorettiexchange.presentation.monthlybalancing.components.SummaryCard
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBalancingScreen(
    onMenuClick: () -> Unit,
    viewModel: MonthlyBalancingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatUSD = NumberFormat.getCurrencyInstance(Locale.US)
    val formatPEN = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    // Animación
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuadre Mensual") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    // BOTÓN REFRESH
                    IconButton(onClick = {
                        scope.launch {
                            rotation.animateTo(
                                targetValue = rotation.value + 360f,
                                animationSpec = tween(durationMillis = 1000)
                            )
                        }
                        viewModel.refresh()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White,
                            modifier = Modifier.rotate(rotation.value)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF092B5A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    FilterSection(
                        selectedYear = uiState.selectedYear,
                        years = uiState.years,
                        selectedMonthIndex = uiState.selectedMonthIndex,
                        onYearSelected = { viewModel.onYearSelected(it) },
                        onMonthSelected = { viewModel.onMonthSelected(it) }
                    )
                }

                item {
                    val summary = uiState.summary
                    if (summary != null) {
                        SummarySection(
                            compraUSD = formatUSD.format(summary.totalCompraUSD),
                            compraSoles = formatPEN.format(summary.totalCompraSoles),
                            ventaUSD = formatUSD.format(summary.totalVentaUSD),
                            ventaSoles = formatPEN.format(summary.totalVentaSoles),
                            utilidad = formatPEN.format(summary.utilidad),
                            tasa = String.format("%.3f", summary.tasaPromedio) // Formato bonito para tasa
                        )
                    }
                }

                if (uiState.purchaseMovements.isNotEmpty()) {
                    item {
                        MovementSection(
                            title = "Movimientos de Compra",
                            movements = uiState.purchaseMovements
                        )
                    }
                }

                if (uiState.saleMovements.isNotEmpty()) {
                    item {
                        MovementSection(
                            title = "Movimientos de Venta",
                            movements = uiState.saleMovements
                        )
                    }
                }
            }
        }
    }
}

// ... (Resto de componentes auxiliares: FilterSection, SummarySection, MovementSection, etc. se mantienen igual que antes)
// Asegúrate de copiar también las funciones auxiliares que te pasé en la respuesta anterior si no las tienes en otro archivo.
@Composable
fun FilterSection(
    selectedYear: Int,
    years: List<Int>,
    selectedMonthIndex: Int,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    var expandedYear by remember { mutableStateOf(false) }
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF092B5A))
            .padding(16.dp)
    ) {
        Text("Año", color = Color.White.copy(alpha = 0.8f))
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expandedYear = true }
            ) {
                Text(selectedYear.toString(), color = Color.White, fontSize = 18.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
            }
            DropdownMenu(expanded = expandedYear, onDismissRequest = { expandedYear = false }) {
                years.forEach { year ->
                    DropdownMenuItem(text = { Text(year.toString()) }, onClick = { onYearSelected(year); expandedYear = false })
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Mes", color = Color.White.copy(alpha = 0.8f))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(months) { index, month ->
                val isSelected = index == selectedMonthIndex
                Button(
                    onClick = { onMonthSelected(index) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFFFFFFF) else Color(0x33FFFFFF),
                        contentColor = if (isSelected) Color(0xFF092B5A) else Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(month, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SummarySection(compraUSD: String, compraSoles: String, ventaUSD: String, ventaSoles: String, utilidad: String, tasa: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Resumen General", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingDown, "Total Compra USD", compraUSD, Color(0xFFC62828))
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingDown, "Total Compra Soles", compraSoles, Color(0xFFC62828))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingUp, "Total Venta USD", ventaUSD, Color(0xFF2E7D32))
                SummaryCard(Modifier.weight(1f), Icons.Default.TrendingUp, "Total Venta Soles", ventaSoles, Color(0xFF2E7D32))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard(Modifier.weight(1f), Icons.Default.AttachMoney, "Utilidad", utilidad, Color(0xFFF9A825))
                SummaryCard(Modifier.weight(1f), Icons.Default.CompareArrows, "Tasa Promedio", tasa, Color(0xFF1565C0))
            }
        }
    }
}

@Composable
fun MovementSection(title: String, movements: List<Transaccion>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF374151))
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            movements.forEach { movement -> MovementListItem(movement = movement) }
        }
    }
}