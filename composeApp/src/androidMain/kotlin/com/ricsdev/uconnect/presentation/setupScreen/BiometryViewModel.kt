package com.ricsdev.uconnect.presentation.setupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BiometryViewModel(
    val biometryAuthenticator: BiometryAuthenticator
) : ViewModel() {
    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    fun loginWithBiometric() {
        viewModelScope.launch {
            try {
                val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                    requestTitle = "Biometry".desc(),
                    requestReason = "Just for test".desc(),
                    failureButtonText = "Oops".desc(),
                    allowDeviceCredentials = false
                )

                if (isSuccess) {
                    _result.value = "Authentication successful"
                } else {
                    _result.value = "Authentication failed"
                }
            } catch (throwable: Throwable) {
                _result.value = "Authentication error: ${throwable.message}"
            }
        }
    }
}