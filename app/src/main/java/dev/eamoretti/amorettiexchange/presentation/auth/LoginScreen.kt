package dev.eamoretti.amorettiexchange.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Efecto para navegar si el login es exitoso
    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ------- HEADER SUPERIOR --------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF092B5A))
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Amoretti Exchange",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sistema de Operaciones",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        // -------- CUERPO --------
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Bienvenido", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Accede a tu historial de transacciones", fontSize = 14.sp, color = Color.Gray)

            Spacer(Modifier.height(24.dp))

            // Mensaje de Error
            if (viewModel.loginError != null) {
                Text(
                    text = viewModel.loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("usuario@app.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() })
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.login()
                    focusManager.clearFocus()
                })
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A1A2F)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar Sesión", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}