package com.ricsdev.uconnect.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.model.OtpType
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.domain.usecase.GetAllAccountsUseCase
import com.ricsdev.uconnect.util.CryptoManager
import com.ricsdev.uconnect.util.twoFa.OtpManager
import com.ricsdev.uconnect.util.twoFa.TotpGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class HomeViewModel(
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val cryptoManager: CryptoManager,
    private val otpManager: OtpManager
) : ViewModel() {

    private val _accountsState = MutableStateFlow<UiState<List<Account>>>(UiState.Loading)
    val accountsState: StateFlow<UiState<List<Account>>> = _accountsState

    private val otpMap = mutableMapOf<Int, MutableStateFlow<String?>>()
    private val remainingTimeMap = mutableMapOf<Int, MutableStateFlow<Int>>()

    init {
        fetchAccounts()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                getAllAccountsUseCase().collect { accounts ->
                    _accountsState.value = UiState.Success(accounts)
                    accounts.forEach { account ->
                        if (account.twoFaSettings != null) {
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
                otpMap[accountId]?.value = otp

                if (settings.type == OtpType.TOTP) {
                    val remainingTime = (otpManager.timeslotLeft(settings, timestamp) * settings.period.millis).toInt()
                    remainingTimeMap[accountId]?.value = remainingTime / 1000 // Convert to seconds
                    delay(1000) // Update every second
                } else {
                    delay(30_000) // Default delay for HOTP
                }
            }
        }
    }

    fun getCurrentOtp(accountId: Int): StateFlow<String?> {
        return otpMap.getOrPut(accountId) { MutableStateFlow(null) }
    }

    fun getRemainingTime(accountId: Int): StateFlow<Int> {
        return remainingTimeMap.getOrPut(accountId) { MutableStateFlow(30) }
    }
}