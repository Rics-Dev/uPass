package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.repository.Repository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetAccountUseCase: KoinComponent {
    private val repository: Repository by inject()
    suspend fun execute(id: Int) = repository.getAccount(id)
}