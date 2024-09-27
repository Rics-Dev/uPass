package com.ricsdev.uconnect.domain.usecase

import com.ricsdev.uconnect.domain.repository.Repository

class GetAccountUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(id: Int) = repository.getAccount(id)
}