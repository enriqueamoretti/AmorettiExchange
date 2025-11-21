package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// Importante: Usar el viewModel de Compose y nuestro componente actualizado
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.eamoretti.amorettiexchange.presentation.transactions.components.TransactionListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onMenuClick: () -> Unit,
    onNavigateToRegisterTransaction: () -> Unit,
    // Inyectamos el ViewModel
    viewModel: TransactionsViewModel = viewModel()
) {
    // Estado que viene de la API
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // TopBar Azul
            TopAppBar(
                title = { Text("Transacciones") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegisterTransaction,
                containerColor = Color(0xFF0A1A2F),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar Transacción")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // --- Buscador ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.onSearch(it) // Llama al VM para buscar en API
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar transacción...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(12.dp)
            )

            // --- Filtros (Todas, Compras, Ventas) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filterType == null,
                    onClick = { viewModel.onFilterChanged(null) },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0A1A2F),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = uiState.filterType == 1,
                    onClick = { viewModel.onFilterChanged(1) }, // 1 = Compras
                    label = { Text("Compras") },
                    leadingIcon = { if (uiState.filterType == 1) Icon(Icons.Default.Check, null) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0A1A2F), selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = uiState.filterType == 2,
                    onClick = { viewModel.onFilterChanged(2) }, // 2 = Ventas
                    label = { Text("Ventas") },
                    leadingIcon = { if (uiState.filterType == 2) Icon(Icons.Default.Check, null) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0A1A2F), selectedLabelColor = Color.White)
                )
            }

            Spacer(Modifier.height(8.dp))

            // --- Lista de Resultados ---
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error ?: "Error",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.transactions) { transaction ->
                                TransactionListItem(transaction = transaction)
                            }
                        }
                    }
                }
            }
        }
    }
}