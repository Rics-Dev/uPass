package com.ricsdev.uconnect.presentation.setupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

actual class SetupViewModel actual constructor(
    private val secureStorage: SecureStorage,
    private val biometryAuthenticator: Any?
) : ViewModel() {


    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.Loading)
    actual val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
//            secureStorage.reset()
            val isMasterPasswordSet = secureStorage.isMasterPasswordSet()
            _uiState.value = if (isMasterPasswordSet) {
                SetupUiState.Login()
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

    actual fun login(password: String) {
        viewModelScope.launch {
            val isPasswordCorrect = secureStorage.verifyMasterPassword(password)
            if (isPasswordCorrect) {
                _uiState.value = SetupUiState.NavigateToHome
            } else {
                _uiState.value = SetupUiState.Login(error = "Incorrect Master Password")
            }
        }
    }



    actual fun enableBiometrics() {

    }
}