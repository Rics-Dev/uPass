package com.ricsdev.upass.presentation.setupScreen

import androidx.compose.runtime.Composable

@Composable
actual fun LoginContent(
    viewModel: SetupViewModel,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
) {
}

@Composable
actual fun BiometricScreen(
    viewModel: SetupViewModel,
    error: String?
) {
}