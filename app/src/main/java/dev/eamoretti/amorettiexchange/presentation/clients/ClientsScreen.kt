package dev.eamoretti.amorettiexchange.presentation.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.eamoretti.amorettiexchange.presentation.clients.components.ClientListItem

data class Client(
    val name: String,
    val ruc: String,
    val phone: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    onMenuClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val clients = listOf(
        Client("INVERSIONES Y NEGOCIOS CORPORATIVOS G.P.", "20611921701", "959224270"),
        Client("INMOBILIARIA VALEISA SAC", "20510510449", "912567088"),
        Client("BARRERA BENAVIDES JUANA MARIA LUISA", "08208278", "908708558"),
        Client("CARLOS ALEJANDRO CARRIONOL RAYGADA", "45371282", "989163077")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
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
                onClick = { /* TODO: Navigate to client registration */ },
                containerColor = Color(0xFF0A1A2F),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Cliente")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar cliente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") }
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clients.filter { it.name.contains(searchQuery, ignoreCase = true) }) { client ->
                    ClientListItem(client = client)
                }
            }
        }
    }
}