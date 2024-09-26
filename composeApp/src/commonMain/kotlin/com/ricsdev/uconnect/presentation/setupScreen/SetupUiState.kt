package com.ricsdev.uconnect.presentation.setupScreen

sealed class SetupUiState {
    data object Loading : SetupUiState()
    data class InitialSetup(var error: String? = null, val isBiometricEnabled: Boolean = false) : SetupUiState()
    data class Login(val error: String? = null, val isBiometricEnabled: Boolean = false) : SetupUiState()
    data object NavigateToHome : SetupUiState()
}