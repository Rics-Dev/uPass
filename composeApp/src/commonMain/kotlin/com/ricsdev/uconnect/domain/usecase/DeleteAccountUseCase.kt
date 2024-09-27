package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.repository.Repository

class DeleteAccountUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(account: Account){
        repository.deleteAccount(account)
    }
}