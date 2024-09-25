package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.runtime.Composable

@Composable
actual fun LoginContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
) {
}