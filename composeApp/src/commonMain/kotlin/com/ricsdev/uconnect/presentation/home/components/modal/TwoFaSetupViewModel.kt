package com.ricsdev.uconnect.presentation.home.components.modal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.domain.usecase.SaveAccountUseCase
import com.ricsdev.uconnect.util.twoFa.OtpManager
import com.ricsdev.uconnect.util.twoFa.isValidBase32Secret
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class TwoFaSetupViewModel(
    private val saveAccountUseCase: SaveAccountUseCase,
) : ViewModel() {

    private val _accountState = MutableStateFlow(Account())
    val accountState: StateFlow<Account> = _accountState


    fun updateTwoFaSettings(twoFaSettings: TwoFaSettings) {
        _accountState.update {
            it.copy(twoFaSettings = twoFaSettings)
        }
    }


    fun saveAccount() {
        viewModelScope.launch {
            if(_accountState.value.twoFaSettings != null){
                if(_accountState.value.twoFaSettings!!.secret.isValidBase32Secret){
                    saveAccountUseCase.execute(_accountState.value)
                }else{
                    println("Not a valid base 32 secret")
                }

            }else{

            }
        }
    }


}