package com.ricsdev.upass.presentation.home.components.modal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.domain.usecase.SaveAccountUseCase
import com.ricsdev.upass.util.twoFa.isValidBase32
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TwoFaSetupViewModel(
    private val saveAccountUseCase: SaveAccountUseCase,
) : ViewModel() {

    private val _accountState = MutableStateFlow(Account())
    val accountState: StateFlow<Account> = _accountState


    fun updateAccountState(account: Account) {
        _accountState.update { account }
    }


    fun saveAccount() {
        viewModelScope.launch {
            val twoFaSettings = _accountState.value.twoFaSettings
            if (twoFaSettings == null) {
                println("TwoFaSettings is null")
                return@launch
            }
            if (!twoFaSettings.secret.isValidBase32) {
                println("Not a valid base 32 secret")
                return@launch
            }
            saveAccountUseCase(_accountState.value)
        }
    }


}