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


    private val _accountsState = MutableStateFlow<UiState<List<Account>>>(UiState.Loading)
    val accountsState: StateFlow<UiState<List<Account>>> = _accountsState

    init {
        fetchAccounts()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                getAllAccountsUseCase().collect { accounts ->
                    _accountsState.value = UiState.Success(accounts)
                }
            } catch (e: Exception) {
                _accountsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}