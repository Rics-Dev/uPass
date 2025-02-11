package com.ricsdev.upass.domain.usecase

import com.ricsdev.upass.domain.repository.Repository

class GetAccountUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(id: Int) = repository.getAccount(id)
}