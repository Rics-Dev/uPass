package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetAllAccountsUseCase(private val repository: Repository) {
    operator fun invoke(): Flow<List<Account>> = repository.getAllAccounts()
}