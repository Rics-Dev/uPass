package com.ricsdev.upass.presentation.setupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.upass.util.SecureStorage
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

actual class SetupViewModel actual constructor(
    private val secureStorage: SecureStorage,
    val biometryAuthenticator: Any?
) : ViewModel() {


    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.Loading)
    actual val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            val isMasterPasswordSet = secureStorage.isMasterPasswordSet()
            val isBiometricEnabled = secureStorage.isBiometricEnabled()
            _uiState.value = if (isMasterPasswordSet) {
                SetupUiState.Login(isBiometricEnabled = isBiometricEnabled)
            } else {
                SetupUiState.InitialSetup()
            }
        }
    }

    actual suspend fun setMasterPassword(password: String, confirmPassword: String): Boolean {
        val result = CompletableDeferred<Boolean>()
        viewModelScope.launch {
            when {
                password.isBlank() -> {
                    _uiState.value = SetupUiState.InitialSetup(error = "Password cannot be empty")
                    result.complete(false)
                }

                password != confirmPassword -> {
                    _uiState.value = SetupUiState.InitialSetup(error = "Passwords do not match")
                    result.complete(false)
                }

                else -> {
                    secureStorage.setMasterPassword(password)
                    result.complete(true)
                }
            }
        }
        return result.await()
    }


    actual fun enableBiometrics() {
        viewModelScope.launch {
            try {
                val isSuccess =
                    (biometryAuthenticator as BiometryAuthenticator).checkBiometryAuthentication(
                        requestTitle = "Biometry".desc(),
                        requestReason = "Authenticate with fingerprint or face recognition".desc(),
                        failureButtonText = "Cancel".desc(),
                        allowDeviceCredentials = false
                    )

                if (isSuccess) {
                    secureStorage.saveBiometricState(true)
                    _uiState.value = SetupUiState.InitialSetup(isBiometricEnabled = true)
                } else {
                    _uiState.value =
                        SetupUiState.InitialSetup(error = "Biometry authentication failed")
                }
            } catch (throwable: Throwable) {
                _uiState.value = SetupUiState.Login(
                    error = "Authentication error: ${throwable.message}",
                    isBiometricEnabled = false
                )
            }
        }
    }


    fun loginWithBiometric() {
        viewModelScope.launch {
            try {
                val isSuccess =
                    (biometryAuthenticator as BiometryAuthenticator).checkBiometryAuthentication(
                        requestTitle = "Biometry".desc(),
                        requestReason = "Authenticate with fingerprint or face recognition".desc(),
                        failureButtonText = "Cancel".desc(),
                        allowDeviceCredentials = false
                    )

                if (isSuccess) {
                    _uiState.value = SetupUiState.NavigateToHome
                } else {
                    _uiState.value = SetupUiState.Login()
                }
            } catch (throwable: Throwable) {
                _uiState.value = SetupUiState.Login(
                    error = "Authentication error: ${throwable.message}",
                    isBiometricEnabled = false
                )
            }
        }
    }


    actual fun login(password: String) {
        viewModelScope.launch {
            val isPasswordCorrect = secureStorage.verifyMasterPassword(password)
            if (isPasswordCorrect) {
                _uiState.value = SetupUiState.NavigateToHome
            } else {
                _uiState.value = SetupUiState.Login(
                    error = "Incorrect password",
                    isBiometricEnabled = false
                )
            }
        }
    }

}