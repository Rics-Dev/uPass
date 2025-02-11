package com.ricsdev.upass.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.domain.model.OtpType
import com.ricsdev.upass.domain.model.TwoFaSettings
import com.ricsdev.upass.domain.model.Vault
import com.ricsdev.upass.domain.usecase.GetAllAccountsUseCase
import com.ricsdev.upass.domain.usecase.SaveAccountUseCase
import com.ricsdev.upass.util.twoFa.OtpManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json


class HomeViewModel(
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val otpManager: OtpManager
) : ViewModel() {

    private val _accountsState = MutableStateFlow<UiState<List<Account>>>(UiState.Loading)
    val accountsState: StateFlow<UiState<List<Account>>> = _accountsState

    private val _otpMap = MutableStateFlow<Map<Int, String?>>(emptyMap())
    val otpMap: StateFlow<Map<Int, String?>> = _otpMap

    private val _remainingTimeMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val remainingTimeMap: StateFlow<Map<Int, Int>> = _remainingTimeMap


    init {
        fetchAccounts()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                getAllAccountsUseCase().collect { accounts ->
                    _accountsState.value = UiState.Success(accounts)
                    accounts.forEach { account ->
                        if (account.twoFaSettings?.secret!!.isNotEmpty()) {
                            generateOtp(account.id, account.twoFaSettings)
                        }
                    }
                }
            } catch (e: Exception) {
                _accountsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun generateOtp(accountId: Int, settings: TwoFaSettings) {
        viewModelScope.launch {
            while (isActive) {
                val timestamp = Clock.System.now().toEpochMilliseconds()
                val otp = otpManager.generateOtp(settings, timestamp)
                _otpMap.value = _otpMap.value.toMutableMap().apply { put(accountId, otp) }

                if (settings.type == OtpType.TOTP) {
                    val remainingTime = (otpManager.timeslotLeft(
                        settings,
                        timestamp
                    ) * settings.period.millis).toInt()
                    _remainingTimeMap.value = _remainingTimeMap.value.toMutableMap()
                        .apply { put(accountId, remainingTime / 1000) }
                    delay(500) // Update every second
                }
            }
        }
    }


    fun importVault(jsonContent: String) {
        viewModelScope.launch {
            try {
                val json = Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
                val vault = json.decodeFromString<Vault>(jsonContent)
                println("Vault: $vault")
                vault.items?.forEach { item ->
                    val account = if (item.login != null) {
                        Account(
                            name = item.name ?: "",
                            username = item.login.username ?: "",
                            password = item.login.password ?: "",
                        )
                    } else {
                        Account(
                            name = item.name ?: "",
                            note = "Item details: ${item.notes ?: "No notes"}"
                        )
                    }
                    saveAccountUseCase(account)
                }
                // Show success message
            } catch (e: Exception) {
                println("Error importing vault: ${e.message}")
            }
        }
    }


}