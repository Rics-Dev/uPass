package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.repository.Repository

class SaveAccountUseCase(
    private val repository: Repository
) {
    suspend fun execute(account: Account) = repository.saveAccount(account)
}