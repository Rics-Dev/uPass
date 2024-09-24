package com.ricsdev.uconnect.util

import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64

actual class SecureStorage {
    private val preferences = Preferences.userNodeForPackage(SecureStorage::class.java)

    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val encodedKey = Base64.getEncoder().encodeToString(key)
            preferences.put("aes_key", encodedKey)
            preferences.flush()
        } catch (e: Exception) {
            // Handle exception
        }
    }

    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val encodedKey = preferences.get("aes_key", null)
            encodedKey?.let { Base64.getDecoder().decode(it) }
        } catch (e: Exception) {
            // Handle exception
            null
        }
    }
}