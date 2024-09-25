package com.ricsdev.uconnect.presentation.setupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.util.BiometricAuth
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SetupViewModel(
    private val secureStorage: SecureStorage,
    private val biometricAuth: BiometricAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.InitialSetup())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            biometricAuth.loadBiometricState()
            val isMasterPasswordSet = secureStorage.isMasterPasswordSet().first()
            val isBiometricEnabled = biometricAuth.isBiometricEnabled()
            _uiState.value = if (isMasterPasswordSet) {
                SetupUiState.Login(isBiometricEnabled = isBiometricEnabled)
            } else {
                SetupUiState.InitialSetup()
            }
        }
    }

    fun setMasterPassword(password: String, useBiometric: Boolean) {
        viewModelScope.launch {
            if (password.length < 8) {
                _uiState.value = SetupUiState.InitialSetup(error = "Password must be at least 8 characters long")
            } else {
                secureStorage.setMasterPassword(password)
                if (useBiometric && biometricAuth.isBiometricAvailable()) {
                    biometricAuth.enableBiometric()
                }
                _uiState.value = SetupUiState.NavigateToHome
            }
        }
    }

    fun login(password: String) {
        viewModelScope.launch {
            val isPasswordCorrect = secureStorage.verifyMasterPassword(password)
            if (isPasswordCorrect) {
                _uiState.value = SetupUiState.NavigateToHome
            } else {
                _uiState.value = SetupUiState.Login(error = "Incorrect password", isBiometricEnabled = biometricAuth.isBiometricEnabled())
            }
        }
    }

    fun isBiometricAvailable(): Boolean = biometricAuth.isBiometricAvailable()
    fun isBiometricEnabled(): Boolean = biometricAuth.isBiometricEnabled()
}