package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun LoginContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
) {
    val viewModel: BiometryViewModel = koinViewModel()

    BindBiometryAuthenticatorEffect(viewModel.biometryAuthenticator)

    val text: String by viewModel.result.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Master Password", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Master Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogin) {
            Text("Login")
        }
        if (isBiometricEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.loginWithBiometric() }) {
                Text("Login with Biometric")
            }
        }
    }
}