package com.ricsdev.uconnect.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.usecase.GetAllAccountsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getAllAccountsUseCase: GetAllAccountsUseCase
) : ViewModel() {

    private val _accountsState = MutableStateFlow<List<Account>>(emptyList())
    val accountsState: StateFlow<List<Account>> = _accountsState

    init {
        fetchAccounts()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            getAllAccountsUseCase().collect { accounts ->
                _accountsState.value = accounts
            }
        }
    }
}