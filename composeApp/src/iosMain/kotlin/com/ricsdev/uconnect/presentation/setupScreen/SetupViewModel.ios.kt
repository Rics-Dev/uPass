package com.ricsdev.uconnect.presentation.setupScreen

import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.flow.StateFlow

actual class SetupViewModel actual constructor(secureStorage: SecureStorage) {
    actual val uiState: StateFlow<SetupUiState>
        get() = TODO("Not yet implemented")

    actual fun setMasterPassword(password: String, useBiometric: Boolean) {
    }

    actual fun login(password: String) {
    }

    actual fun isBiometricAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isBiometricEnabled(): Boolean {
        TODO("Not yet implemented")
    }
}