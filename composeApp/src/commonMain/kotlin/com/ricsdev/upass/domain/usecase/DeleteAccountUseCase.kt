package com.ricsdev.upass.domain.usecase

import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.domain.repository.Repository

class DeleteAccountUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(account: Account) {
        repository.deleteAccount(account)
    }
}