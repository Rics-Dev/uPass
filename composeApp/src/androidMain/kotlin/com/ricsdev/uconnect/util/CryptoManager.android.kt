package com.ricsdev.uconnect.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

actual class SecureStorage(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.edit().putString("aes_key", key.encodeBase64()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.getString("aes_key", null)?.decodeBase64()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    actual suspend fun setMasterPassword(password: String) = withContext(Dispatchers.IO) {
        try {
            sharedPreferences.edit().putString("master_password", password).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    actual fun isMasterPasswordSet(): Flow<Boolean> = flow {
        emit(sharedPreferences.contains("master_password"))
    }

    private fun ByteArray.encodeBase64(): String = android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)
    private fun String.decodeBase64(): ByteArray = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
}