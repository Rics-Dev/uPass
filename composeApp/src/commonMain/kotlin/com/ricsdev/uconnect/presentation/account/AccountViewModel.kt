package com.ricsdev.uconnect.presentation.account

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    fun updateAccountName(name: String) {
        _uiState.update { it.copy(account = it.account.copy(name = name)) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(account = it.account.copy(username = username)) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(account = it.account.copy(password = password)) }
    }

    fun updateUrl(index: Int, url: String) {
        _uiState.update {
            val updatedUrls = it.account.urls.toMutableList()
            updatedUrls[index] = url
            it.copy(account = it.account.copy(urls = updatedUrls))
        }
    }

    fun addUrl() {
        _uiState.update {
            it.copy(account = it.account.copy(urls = it.account.urls + ""))
        }
    }

    fun removeUrl(index: Int) {
        _uiState.update {
            val updatedUrls = it.account.urls.toMutableList()
            updatedUrls.removeAt(index)
            it.copy(account = it.account.copy(urls = updatedUrls))
        }
    }

    fun initializeTwoFaSettings() {
        _uiState.update {
            it.copy(account = it.account.copy(twoFaSettings = TwoFaSettings()))
        }
    }

    fun updateTwoFaSettings(twoFaSettings: TwoFaSettings) {
        _uiState.update {
            it.copy(account = it.account.copy(twoFaSettings = twoFaSettings))
        }
    }

    fun updateCustomField(index: Int, field: CustomField) {
        _uiState.update {
            val updatedFields = it.account.customFields.toMutableList()
            updatedFields[index] = field
            it.copy(account = it.account.copy(customFields = updatedFields))
        }
    }

    fun addCustomField() {
        _uiState.update {
            it.copy(account = it.account.copy(
                customFields = it.account.customFields + CustomField(CustomFieldType.TEXT, "New Field")
            ))
        }
    }

    fun removeCustomField(index: Int) {
        _uiState.update {
            val updatedFields = it.account.customFields.toMutableList()
            updatedFields.removeAt(index)
            it.copy(account = it.account.copy(customFields = updatedFields))
        }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(account = it.account.copy(note = note)) }
    }

    fun saveAccount() {
        // Implement the logic to save the account to your data source
        // For example, you might call a repository method here
        println("Saving account: ${_uiState.value.account}")
    }
}

data class AccountUiState(
    val account: Account = Account()
)