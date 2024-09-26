package com.ricsdev.uconnect.presentation.setupScreen

import androidx.lifecycle.ViewModel
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.flow.StateFlow



expect class SetupViewModel(
    secureStorage: SecureStorage,
    biometryAuthenticator: Any? = null,
): ViewModel {
    val uiState: StateFlow<SetupUiState>
    suspend fun setMasterPassword(password: String, confirmPassword: String): Boolean
    fun login(password: String)
    fun enableBiometrics()
}