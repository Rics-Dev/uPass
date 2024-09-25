package com.ricsdev.uconnect.presentation.setupScreen

sealed class SetupUiState {
    data class InitialSetup(val error: String? = null) : SetupUiState()
    data class Login(val error: String? = null, val isBiometricEnabled: Boolean = false) : SetupUiState()
    object NavigateToHome : SetupUiState()
}