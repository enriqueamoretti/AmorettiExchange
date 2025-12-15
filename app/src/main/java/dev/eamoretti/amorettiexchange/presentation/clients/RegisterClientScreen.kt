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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterClientScreen(
    onNavigateBack: () -> Unit,
    // Inyectamos el nuevo ViewModel
    viewModel: RegisterClientViewModel = viewModel()
) {
    // Efecto para navegar atrás si se guardó con éxito
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            onNavigateBack()
            viewModel.resetState()
        }
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
        // Contenedor principal con Scroll
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- FORMULARIO ---
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Mensaje de Error General (si falla la API)
                    if (viewModel.errorMessage != null) {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Nombre (Obligatorio)
                    FormField(
                        label = "Nombre / Razón Social *",
                        value = viewModel.name,
                        onValueChange = {
                            viewModel.name = it
                            viewModel.isNameError = false
                        },
                        placeholder = "Ingrese el nombre o razón social",
                        leadingIcon = Icons.Default.Business,
                        isError = viewModel.isNameError,
                        errorMessage = "Este campo es obligatorio"
                    )

                    // RUC/DNI
                    FormField(
                        label = "RUC / DNI",
                        value = viewModel.ruc,
                        onValueChange = { viewModel.ruc = it.filter { c -> c.isDigit() } },
                        placeholder = "Solo números",
                        leadingIcon = Icons.Default.CreditCard,
                        keyboardType = KeyboardType.Number
                    )

                    // Teléfono Principal
                    FormField(
                        label = "Teléfono Principal",
                        value = viewModel.mainPhone,
                        onValueChange = {
                            viewModel.mainPhone = it.filter { c -> c.isDigit() }.take(9)
                            viewModel.isPhoneError = false
                        },
                        placeholder = "999999999",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Number,
                        isError = viewModel.isPhoneError,
                        errorMessage = "Debe tener 9 dígitos"
                    )

                    // Teléfono Auxiliar
                    FormField(
                        label = "Teléfono Auxiliar",
                        value = viewModel.auxPhone,
                        onValueChange = { viewModel.auxPhone = it.filter { c -> c.isDigit() }.take(9) },
                        placeholder = "999999999 (opcional)",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Number
                    )

                    // Número de Cuenta
                    FormField(
                        label = "Número de Cuenta",
                        value = viewModel.accountNumber,
                        onValueChange = { viewModel.accountNumber = it.filter { c -> c.isDigit() } },
                        placeholder = "Número de cuenta (opcional)",
                        leadingIcon = Icons.Default.ConfirmationNumber,
                        keyboardType = KeyboardType.Number
                    )

                    // Dirección
                    FormField(
                        label = "Dirección",
                        value = viewModel.address,
                        onValueChange = { viewModel.address = it },
                        placeholder = "Dirección completa (opcional)",
                        isTextArea = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- BOTONES DE ACCIÓN ---
            Column {
                Button(
                    onClick = { viewModel.validateAndSave() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF092B5A)),
                    enabled = !viewModel.isLoading // Deshabilitar si está cargando
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando...")
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Guardar Cliente")
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
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
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isTextArea) Modifier.height(120.dp) else Modifier),
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = !isTextArea,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF092B5A),
                cursorColor = Color(0xFF092B5A)
            )
        )
        if (isError) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
        }
    }
}