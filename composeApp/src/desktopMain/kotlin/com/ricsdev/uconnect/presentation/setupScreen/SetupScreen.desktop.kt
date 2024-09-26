package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
actual fun LoginContent(
    viewModel: SetupViewModel,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
) {

    Icon(Icons.Default.Password, contentDescription = "Password", modifier = Modifier.size(64.dp))


    Spacer(modifier = Modifier.height(32.dp))
    Text("Enter Master Password", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(
                text = error ?: "Master Password",
            )
        },
        visualTransformation = PasswordVisualTransformation(),
        isError = error != null,
        maxLines = 1,
        modifier = Modifier.onPreviewKeyEvent {
            if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                if (password.isNotBlank()) {
                    onLogin()
                }
                true
            } else {
                false
            }
        },
    )
    Spacer(modifier = Modifier.height(32.dp))
    Button(onClick = onLogin) {
        Text("Unlock")
    }
}



@Composable
actual fun BiometricScreen(
    viewModel: SetupViewModel,
    error: String?
) {
}