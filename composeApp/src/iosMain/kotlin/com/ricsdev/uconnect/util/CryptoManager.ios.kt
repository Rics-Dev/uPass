package com.ricsdev.uconnect.util

actual class SecureStorage {
    //    suspend fun reset()
    actual suspend fun saveKey(key: ByteArray) {
    }

    actual suspend fun retrieveKey(): ByteArray? {
        TODO("Not yet implemented")
    }

    actual suspend fun setMasterPassword(password: String) {
    }

    actual suspend fun verifyMasterPassword(password: String): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun isMasterPasswordSet(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun isBiometricEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun saveBiometricState(biometricState: Boolean) {
    }
}