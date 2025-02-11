package com.ricsdev.upass.domain.repository

import com.ricsdev.upass.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun saveAccount(account: Account)
    suspend fun getAccount(id: Int): Account?
    suspend fun deleteAccount(account: Account)
    fun getAllAccounts(): Flow<List<Account>>
}