package com.ricsdev.upass.domain.usecase

import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetAllAccountsUseCase(private val repository: Repository) {
    operator fun invoke(): Flow<List<Account>> = repository.getAllAccounts()
}