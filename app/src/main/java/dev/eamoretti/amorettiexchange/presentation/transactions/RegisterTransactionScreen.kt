package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eamoretti.amorettiexchange.presentation.clients.model.Client
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTransactionScreen(
    onNavigateBack: () -> Unit,
    clients: List<Client>
) {
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var date by remember { mutableStateOf("21/11/2025") }
    var movement by remember { mutableStateOf("Compra") }
    var currency by remember { mutableStateOf("Dólares") }
    var amount by remember { mutableStateOf("") }
    var exchangeRate by remember { mutableStateOf("3.375") }
    var paymentType by remember { mutableStateOf("Efectivo") }
    var totalInSoles by remember { mutableStateOf("S/ 0.00") }
    var detail by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Completada") }
    var isClientDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(amount, exchangeRate, currency) {
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val exchangeRateValue = exchangeRate.toDoubleOrNull() ?: 0.0
        val total = if (currency == "Dólares") amountValue * exchangeRateValue else amountValue
        totalInSoles = "S/ ${DecimalFormat("#,##0.00").format(total)}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Transacción") },
                // This onClick MUST call onNavigateBack to return to the previous screen (Transactions)
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF092B5A), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState())) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // All form fields remain the same
                    ExposedDropdownMenuBox(expanded = isClientDropdownExpanded, onExpandedChange = { isClientDropdownExpanded = !isClientDropdownExpanded }) {
                        OutlinedTextField(
                            value = selectedClient?.name ?: "Seleccione un cliente",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClientDropdownExpanded) }
                        )
                        ExposedDropdownMenu(expanded = isClientDropdownExpanded, onDismissRequest = { isClientDropdownExpanded = false }) {
                            clients.forEach { client ->
                                DropdownMenuItem(text = { Text(client.name) }, onClick = { selectedClient = client; isClientDropdownExpanded = false })
                            }
                        }
                    }
                    FormField(label = "Fecha *", value = date, onValueChange = { date = it }, placeholder = "DD/MM/YYYY", leadingIcon = Icons.Default.DateRange)
                    SelectionGroup(label = "Movimiento *", options = listOf("Compra", "Venta"), selectedOption = movement, onOptionSelect = { movement = it })
                    SelectionGroup(label = "Moneda *", options = listOf("Dólares", "Soles"), selectedOption = currency, onOptionSelect = { currency = it })
                    FormField(label = "Monto *", value = amount, onValueChange = { amount = it }, placeholder = "0.00", leadingIcon = Icons.Default.AttachMoney, keyboardType = KeyboardType.Decimal)
                    if (currency == "Dólares") {
                        FormField(label = "Tipo de Cambio *", value = exchangeRate, onValueChange = { exchangeRate = it }, placeholder = "3.375", leadingIcon = Icons.Default.CompareArrows, keyboardType = KeyboardType.Decimal)
                    }
                    SelectionGroup(label = "Tipo de Pago *", options = listOf("Efectivo", "Transferencia", "Otro"), selectedOption = paymentType, onOptionSelect = { paymentType = it })
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(16.dp)) {
                        Column {
                            Text("Total en Soles", color = Color.Gray, fontSize = 14.sp)
                            Text(totalInSoles, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                        }
                    }
                    OutlinedTextField(value = detail, onValueChange = { detail = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Detalle") }, placeholder = { Text("Información adicional (opcional)")})
                    SelectionGroup(label = "Estado *", options = listOf("Completada", "Pendiente", "Cancelada"), selectedOption = status, onOptionSelect = { status = it })
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { /* TODO: Validation */ onNavigateBack() }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF092B5A))) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Registrar Transacción")
            }
            Spacer(Modifier.height(8.dp))
            // This onClick MUST call onNavigateBack to return to the previous screen (Transactions)
            OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) {
                Text("Cancelar")
            }
        }
    }
}

// Helper composables (SelectionGroup, FormField) remain the same
@Composable
fun SelectionGroup(label: String, options: List<String>, selectedOption: String, onOptionSelect: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                Button(onClick = { onOptionSelect(option) }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF092B5A) else Color.LightGray, contentColor = if (isSelected) Color.White else Color.Black)) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, leadingIcon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}