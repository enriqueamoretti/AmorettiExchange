package dev.eamoretti.amorettiexchange.presentation.clients

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterClientScreen(
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ruc by remember { mutableStateOf("") }
    var mainPhone by remember { mutableStateOf("") }
    var auxPhone by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isNameError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        isNameError = name.isBlank()
        // Only validate phone if it's not blank
        isPhoneError = mainPhone.isNotBlank() && mainPhone.length != 9
        return !isNameError && !isPhoneError
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Cliente") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Name Field
                    FormField(
                        label = "Nombre / Razón Social *",
                        value = name,
                        onValueChange = { name = it; isNameError = false },
                        placeholder = "Ingrese el nombre o razón social",
                        leadingIcon = Icons.Default.Business,
                        isError = isNameError,
                        errorMessage = "Este campo es obligatorio"
                    )
                    // RUC/DNI Field
                    FormField(
                        label = "RUC / DNI",
                        value = ruc,
                        onValueChange = { ruc = it.filter { c -> c.isDigit() } },
                        placeholder = "Solo números",
                        leadingIcon = Icons.Default.CreditCard,
                        keyboardType = KeyboardType.Number
                    )
                    // Main Phone Field
                    FormField(
                        label = "Teléfono Principal",
                        value = mainPhone,
                        onValueChange = { mainPhone = it.filter { c -> c.isDigit() }.take(9); isPhoneError = false },
                        placeholder = "999999999",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Number,
                        isError = isPhoneError,
                        errorMessage = "Debe tener 9 dígitos"
                    )
                    // Auxiliary Phone Field
                    FormField(
                        label = "Teléfono Auxiliar",
                        value = auxPhone,
                        onValueChange = { auxPhone = it.filter { c -> c.isDigit() }.take(9) },
                        placeholder = "999999999 (opcional)",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Number
                    )
                    // Account Number Field
                    FormField(
                        label = "Número de Cuenta",
                        value = accountNumber,
                        onValueChange = { accountNumber = it.filter { c -> c.isDigit() } },
                        placeholder = "Número de cuenta (opcional)",
                        leadingIcon = Icons.Default.ConfirmationNumber,
                        keyboardType = KeyboardType.Number
                    )
                    // Address Field
                    FormField(
                        label = "Dirección",
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "Dirección completa (opcional)",
                        isTextArea = true
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))

            Column {
                Button(
                    onClick = { if (validate()) { onNavigateBack() } },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF092B5A))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Guardar Cliente")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
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
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    isTextArea: Boolean = false
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(if (isTextArea) Modifier.height(120.dp) else Modifier),
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = !isTextArea
        )
        if (isError) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
        }
    }
}