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
                _otpMap.value = _otpMap.value.toMutableMap().apply { put(accountId, otp) }

                if (settings.type == OtpType.TOTP) {
                    val remainingTime = (otpManager.timeslotLeft(settings, timestamp) * settings.period.millis).toInt()
                    _remainingTimeMap.value = _remainingTimeMap.value.toMutableMap().apply { put(accountId, remainingTime / 1000) }
                    delay(500) // Update every second
                }
            }
        }
    }


}