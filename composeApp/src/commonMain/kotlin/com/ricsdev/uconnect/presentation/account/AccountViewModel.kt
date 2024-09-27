package com.ricsdev.uconnect.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.model.CustomField
import com.ricsdev.uconnect.domain.model.CustomFieldType
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.domain.usecase.DeleteAccountUseCase
import com.ricsdev.uconnect.domain.usecase.GetAccountUseCase
import com.ricsdev.uconnect.domain.usecase.SaveAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {
    private val _accountState = MutableStateFlow(Account())
    val accountState: StateFlow<Account> = _accountState


    fun fetchAccountDetails(accountId: Int) {
        viewModelScope.launch {
            val account = getAccountUseCase(accountId)
            _accountState.value = account ?: Account()
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            println(account.id)
            deleteAccountUseCase(account)
        }
    }


    fun updateAccountState(account: Account) {
        _accountState.update { account }
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


    fun saveAccount() {
        viewModelScope.launch {
            val account = _accountState.value
            saveAccountUseCase(account)
        }
    }
}