package com.ricsdev.uconnect.data.repository

import com.ricsdev.uconnect.data.model.AccountEntity
import com.ricsdev.uconnect.data.source.local.dao.AccountDao
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.domain.repository.Repository
import com.ricsdev.uconnect.util.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RepositoryImpl(
    private val accountDao: AccountDao,
    private val cryptoManager: CryptoManager
) : Repository {


    init {
        CoroutineScope(Dispatchers.IO).launch {
            cryptoManager.initializeAesKey()
        }
    }


    override suspend fun saveAccount(account: Account) {
        try {
            val accountData = Json.encodeToString(account).encodeToByteArray()
            val encryptedData = cryptoManager.encrypt(accountData)
            val accountEntity = AccountEntity(encryptedData = encryptedData)
            accountDao.insertAccount(accountEntity)
        } catch (e: Exception) {
            println("Error saving account: ${e.message}")
            // Handle the error appropriately
        }
    }

    override suspend fun getAccount(id: Int): Account? {
        val accountEntity = accountDao.getAccount(id) ?: return null
        val decryptedData = cryptoManager.decrypt(accountEntity.encryptedData)
        if (decryptedData.isEmpty()) {
            return null
        }
        val account = Json.decodeFromString<Account>(decryptedData.decodeToString())
        return account.copy(id = accountEntity.id)
    }


    override suspend fun deleteAccount(account: Account) {
        try {
            val accountEntity = accountDao.getAccount(account.id) ?: return
            accountDao.deleteAccount(accountEntity)
        } catch (e: Exception) {
            println("Error deleting account: ${e.message}")
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { accountEntities ->
            accountEntities.mapNotNull { entity ->
                try {
                    val decryptedData = cryptoManager.decrypt(entity.encryptedData)
                    if (decryptedData.isEmpty()) {
                        null
                    } else {
                        val account = Json.decodeFromString<Account>(decryptedData.decodeToString())
                        account.copy(id = entity.id)
                    }
                } catch (e: Exception) {
                    println("Error decoding account entity: ${e.message}")
                    null
                }
            }
        }
}