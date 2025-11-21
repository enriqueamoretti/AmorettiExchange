package dev.eamoretti.amorettiexchange.presentation.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.eamoretti.amorettiexchange.presentation.transactions.components.Transaction
import dev.eamoretti.amorettiexchange.presentation.transactions.components.TransactionListItem
import dev.eamoretti.amorettiexchange.presentation.transactions.components.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onMenuClick: () -> Unit,
    onNavigateToRegisterTransaction: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    val transactions = listOf(
        Transaction(TransactionType.SALE, "17/11/2025", "JAVIER ENRIQUE LUDOWIEG TELGE", "$ 6,614.30", "S/ 22,422.48", "Efectivo", "Completada"),
        Transaction(TransactionType.SALE, "17/11/2025", "NEGOCIOS JORDI SRL", "$ 6,614.30", "S/ 22,422.48", "Efectivo", "Completada"),
        Transaction(TransactionType.PURCHASE, "17/11/2025", "MARTINEZ LACHIRA MARIETA", "$ 12,000.00", "S/ 40,260.00", "Efectivo", "Completada"),
        Transaction(TransactionType.SALE, "17/11/2025", "INVERSIONES OGOSI SAC", "$ 5,000.00", "S/ 16,815.00", "Efectivo", "Completada")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transacciones") },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = "Menú") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF092B5A), titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
                    Icon(Icons.Default.Add, contentDescription = "Registrar Transacción")
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar Transacción")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar transacción...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
            )
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("Todas", selectedFilter) { selectedFilter = "Todas" }
                FilterChip("Compras", selectedFilter) { selectedFilter = "Compras" }
                FilterChip("Ventas", selectedFilter) { selectedFilter = "Ventas" }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val filteredList = transactions.filter {
                    val matchesSearch = it.clientName.contains(searchQuery, ignoreCase = true)
                    val matchesFilter = when (selectedFilter) {
                        "Compras" -> it.type == TransactionType.PURCHASE
                        "Ventas" -> it.type == TransactionType.SALE
                        else -> true
                    }
                    matchesSearch && matchesFilter
                }
                items(filteredList) { transaction ->
                    TransactionListItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selectedFilter: String, onClick: () -> Unit) {
    val isSelected = label == selectedFilter
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF0A1A2F) else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(label)
    }
}