package com.ricsdev.uconnect.domain.repository

import com.ricsdev.uconnect.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun saveAccount(account: Account)
    suspend fun getAccount(id: Int): Account?
    suspend fun deleteAccount(account: Account)
    fun getAllAccounts(): Flow<List<Account>>
}