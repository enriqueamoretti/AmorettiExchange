package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.eamoretti.amorettiexchange.presentation.transactions.components.TransactionListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onMenuClick: () -> Unit,
    onNavigateToRegisterTransaction: () -> Unit,
    viewModel: TransactionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Animación
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text("Transacciones") },
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

                // Filtros dentro de la barra azul (como en tu diseño)
                Surface(
                    color = Color(0xFF092B5A),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.onSearch(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar transacción...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1A3A6A),
                                unfocusedContainerColor = Color(0xFF1A3A6A),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = uiState.filterType == null,
                                onClick = { viewModel.onFilterChanged(null) },
                                label = { Text("Todas") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1E40AF),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF092B5A)
                                ),
                                border = null, shape = RoundedCornerShape(50)
                            )
                            FilterChip(
                                selected = uiState.filterType == 1,
                                onClick = { viewModel.onFilterChanged(1) },
                                label = { Text("Compras") },
                                leadingIcon = { Icon(Icons.Default.TrendingDown, null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = Color(0xFF092B5A),
                                    containerColor = Color(0xFF1A3A6A),
                                    labelColor = Color.White
                                ),
                                border = null, shape = RoundedCornerShape(50)
                            )
                            FilterChip(
                                selected = uiState.filterType == 2,
                                onClick = { viewModel.onFilterChanged(2) },
                                label = { Text("Ventas") },
                                leadingIcon = { Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = Color(0xFF092B5A),
                                    containerColor = Color(0xFF1A3A6A),
                                    labelColor = Color.White
                                ),
                                border = null, shape = RoundedCornerShape(50)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegisterTransaction,
                containerColor = Color(0xFF092B5A),
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar Transacción")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(uiState.error!!, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.transactions) { item ->
                            TransactionListItem(transaction = item)
                        }
                    }
                }
            }
        }
    }
}