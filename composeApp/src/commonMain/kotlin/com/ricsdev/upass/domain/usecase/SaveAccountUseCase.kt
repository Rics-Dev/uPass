package com.ricsdev.upass.domain.usecase

import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.domain.repository.Repository

class SaveAccountUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(account: Account) = repository.saveAccount(account)
}