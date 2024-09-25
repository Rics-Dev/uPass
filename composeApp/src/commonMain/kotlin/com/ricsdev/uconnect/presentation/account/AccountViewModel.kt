package com.ricsdev.uconnect.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.model.CustomField
import com.ricsdev.uconnect.domain.model.CustomFieldType
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.domain.usecase.GetAccountUseCase
import com.ricsdev.uconnect.domain.usecase.SaveAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
) : ViewModel() {
    private val _accountState = MutableStateFlow(Account())
    val accountState: StateFlow<Account> = _accountState


    fun fetchAccountDetails(accountId: Int) {
        viewModelScope.launch {
            val account = getAccountUseCase(accountId)
            _accountState.value = account ?: Account()
        }
    }


    fun updateAccountName(name: String) {
        _accountState.update { it.copy(name = name) }
    }

    fun updateUsername(username: String) {
        _accountState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _accountState.update { it.copy(password = password) }
    }

    fun updateUrl(index: Int, url: String) {
        _accountState.update {
            val updatedUrls = it.urls.toMutableList()
            updatedUrls[index] = url
            it.copy(urls = updatedUrls)
        }
    }

    fun addUrl() {
        _accountState.update {
            it.copy(urls = it.urls + "")
        }
    }

    fun removeUrl(index: Int) {
        _accountState.update {
            val updatedUrls = it.urls.toMutableList()
            updatedUrls.removeAt(index)
            it.copy(urls = updatedUrls)
        }
    }

    fun initializeTwoFaSettings() {
        _accountState.update {
            it.copy(twoFaSettings = TwoFaSettings())
        }
    }

    fun updateTwoFaSettings(twoFaSettings: TwoFaSettings) {
        _accountState.update {
            it.copy(twoFaSettings = twoFaSettings)
        }
    }

    fun updateCustomField(index: Int, field: CustomField) {
        _accountState.update {
            val updatedFields = it.customFields.toMutableList()
            updatedFields[index] = field
            it.copy(customFields = updatedFields)
        }
    }

    fun addCustomField() {
        _accountState.update {
            it.copy(
                customFields = it.customFields + CustomField(CustomFieldType.TEXT, "New Field")
            )
        }
    }

    fun removeCustomField(index: Int) {
        _accountState.update {
            val updatedFields = it.customFields.toMutableList()
            updatedFields.removeAt(index)
            it.copy(customFields = updatedFields)
        }
    }

    fun updateNote(note: String) {
        _accountState.update { it.copy(note = note) }
    }

    fun saveAccount() {
        viewModelScope.launch {
            val account = _accountState.value
            saveAccountUseCase.execute(account)
        }
    }
}