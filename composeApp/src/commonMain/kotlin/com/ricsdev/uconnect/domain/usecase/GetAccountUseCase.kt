package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.repository.Repository
import org.koin.core.component.KoinComponent

class GetAccountUseCase(
    private val repository: Repository
): KoinComponent {
    suspend operator fun invoke(id: Int) = repository.getAccount(id)
}