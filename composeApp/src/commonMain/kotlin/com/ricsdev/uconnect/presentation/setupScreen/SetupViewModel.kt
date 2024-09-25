package com.ricsdev.uconnect.presentation.setupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.presentation.home.UiState
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SetupViewModel(
    private val secureStorage: SecureStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.InitialSetup())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isMasterPasswordSet = secureStorage.isMasterPasswordSet().first()
            _uiState.value = if (isMasterPasswordSet) {
                SetupUiState.Login()
            } else {
                SetupUiState.InitialSetup()
            }
        }
    }

    fun setMasterPassword(password: String) {
        viewModelScope.launch {
            if (password.length < 8) {
                _uiState.value = SetupUiState.InitialSetup(error = "Password must be at least 8 characters long")
            } else {
                secureStorage.setMasterPassword(password)
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
                _uiState.value = SetupUiState.Login(error = "Incorrect password")
            }
        }
    }
}


