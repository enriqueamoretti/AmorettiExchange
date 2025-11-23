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
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTransactionScreen(
    onNavigateBack: () -> Unit,
    // Eliminamos la lista 'clients' por parámetro, el ViewModel se encarga
    viewModel: RegisterTransactionViewModel = viewModel()
) {
    // Manejo del Dropdown de Clientes
    var expandedClient by remember { mutableStateOf(false) }
    var clientSearchText by remember { mutableStateOf("") }

    // Efecto de éxito
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            onNavigateBack()
            viewModel.resetState()
        }
    }

    // Recalcular total cuando cambian valores
    LaunchedEffect(viewModel.amount, viewModel.exchangeRate) {
        viewModel.calculateTotal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Transacción") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Mensaje de Error
                    if (viewModel.errorMessage != null) {
                        Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }

                    // --- 1. SELECTOR DE CLIENTE CON BUSCADOR ---
                    ExposedDropdownMenuBox(
                        expanded = expandedClient,
                        onExpandedChange = { expandedClient = !expandedClient }
                    ) {
                        OutlinedTextField(
                            value = if (viewModel.selectedClient == null) clientSearchText else viewModel.selectedClient!!.razonSocial,
                            onValueChange = {
                                clientSearchText = it
                                viewModel.selectedClient = null // Resetear si escribe
                                viewModel.filterClients(it)
                                expandedClient = true
                            },
                            label = { Text("Seleccione un cliente") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClient) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF092B5A),
                                cursorColor = Color(0xFF092B5A)
                            )
                        )

                        if (viewModel.filteredClients.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expandedClient,
                                onDismissRequest = { expandedClient = false }
                            ) {
                                viewModel.filteredClients.forEach { client ->
                                    DropdownMenuItem(
                                        text = { Text(client.razonSocial) },
                                        onClick = {
                                            viewModel.selectedClient = client
                                            clientSearchText = client.razonSocial
                                            expandedClient = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Fecha
                    FormField(
                        label = "Fecha *",
                        value = viewModel.date,
                        onValueChange = { viewModel.date = it },
                        placeholder = "YYYY-MM-DD",
                        leadingIcon = Icons.Default.DateRange
                    )

                    // Movimiento
                    SelectionGroup(
                        label = "Movimiento *",
                        options = listOf("Compra", "Venta"),
                        selectedOption = viewModel.movementType,
                        onOptionSelect = { viewModel.movementType = it }
                    )

                    // Moneda (¡Ahora con Euros!)
                    SelectionGroup(
                        label = "Moneda *",
                        options = listOf("Dólares", "Soles", "Euros"),
                        selectedOption = viewModel.currency,
                        onOptionSelect = { viewModel.currency = it }
                    )

                    // Monto
                    FormField(
                        label = "Monto *",
                        value = viewModel.amount,
                        onValueChange = { viewModel.amount = it },
                        placeholder = "0.00",
                        leadingIcon = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Decimal
                    )

                    // Tipo de Cambio
                    FormField(
                        label = "Tipo de Cambio *",
                        value = viewModel.exchangeRate,
                        onValueChange = { viewModel.exchangeRate = it },
                        placeholder = "3.375",
                        leadingIcon = Icons.Default.CompareArrows,
                        keyboardType = KeyboardType.Decimal
                    )

                    // Tipo de Pago
                    SelectionGroup(
                        label = "Tipo de Pago *",
                        options = listOf("Efectivo", "Transferencia", "Yape/Plin"),
                        selectedOption = viewModel.paymentType,
                        onOptionSelect = { viewModel.paymentType = it }
                    )

                    // Total Calculado (Tarjeta Verde)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Total Calculado (Soles)", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                text = viewModel.totalInSoles,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    // Detalle
                    OutlinedTextField(
                        value = viewModel.detail,
                        onValueChange = { viewModel.detail = it },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        label = { Text("Detalle") },
                        placeholder = { Text("Notas opcionales") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF092B5A))
                    )

                    // Estado
                    SelectionGroup(
                        label = "Estado *",
                        options = listOf("Completada", "Pendiente", "Anulada"),
                        selectedOption = viewModel.status,
                        onOptionSelect = { viewModel.status = it }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón Guardar
            Button(
                onClick = { viewModel.saveTransaction() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF092B5A)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.padding(end = 8.dp))
                    Text("Registrar Transacción")
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun SelectionGroup(label: String, options: List<String>, selectedOption: String, onOptionSelect: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        // Usamos FlowRow si hay muchas opciones o ScrollableRow
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                FilterChip(
                    selected = isSelected,
                    onClick = { onOptionSelect(option) },
                    label = { Text(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF092B5A),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF3F4F6),
                        labelColor = Color.Black
                    ),
                    border = null,
                    shape = RoundedCornerShape(50)
                )
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color(0xFF092B5A)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF092B5A),
                cursorColor = Color(0xFF092B5A)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}