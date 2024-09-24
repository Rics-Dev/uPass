package com.ricsdev.uconnect.util

actual class SecureStorage {
    actual suspend fun saveKey(key: ByteArray) {
    }

    actual suspend fun retrieveKey(): ByteArray? {
        TODO("Not yet implemented")
    }
}