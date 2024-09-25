package com.ricsdev.uconnect.util

import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

//actual class SecureStorage {
//    private val preferences = Preferences.userNodeForPackage(SecureStorage::class.java)
//
//    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
//        try {
//            val encodedKey = Base64.getEncoder().encodeToString(key)
//            preferences.put("aes_key", encodedKey)
//            preferences.flush()
//        } catch (e: Exception) {
//            // Handle exception
//        }
//    }
//
//    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
//        try {
//            val encodedKey = preferences.get("aes_key", null)
//            encodedKey?.let { Base64.getDecoder().decode(it) }
//        } catch (e: Exception) {
//            // Handle exception
//            null
//        }
//    }
//}

actual class SecureStorage {
    private val preferences = Preferences.userNodeForPackage(SecureStorage::class.java)
    private val salt = generateOrRetrieveSalt()

    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val masterPassword = preferences.get("master_password", null)
                ?: throw IllegalStateException("Master password not set")
            val (encryptedKey, iv) = encrypt(key, masterPassword.toCharArray())
            preferences.putByteArray("encrypted_key", encryptedKey)
            preferences.putByteArray("iv", iv)
            preferences.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val masterPassword = preferences.get("master_password", null)
                ?: throw IllegalStateException("Master password not set")
            val encryptedKey = preferences.getByteArray("encrypted_key", null)
            val iv = preferences.getByteArray("iv", null)
            if (encryptedKey != null && iv != null) {
                decrypt(encryptedKey, iv, masterPassword.toCharArray())
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual suspend fun setMasterPassword(password: String) = withContext(Dispatchers.IO) {
        val hashedPassword = hashPassword(password)
        preferences.put("master_password", hashedPassword)
        preferences.flush()
    }

    actual fun isMasterPasswordSet(): Flow<Boolean> = flow {
        emit(preferences.get("master_password", null) != null)
    }

    private fun generateOrRetrieveSalt(): ByteArray {
        val storedSalt = preferences.getByteArray("salt", null)
        return if (storedSalt != null) {
            storedSalt
        } else {
            val newSalt = ByteArray(16)
            SecureRandom().nextBytes(newSalt)
            preferences.putByteArray("salt", newSalt)
            preferences.flush()
            newSalt
        }
    }

    private fun hashPassword(password: String): String {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    private fun deriveKey(password: CharArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
        return factory.generateSecret(spec)
    }

    private fun encrypt(data: ByteArray, password: CharArray): Pair<ByteArray, ByteArray> {
        val secretKey = deriveKey(password)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)
        return Pair(encryptedData, iv)
    }

    private fun decrypt(encryptedData: ByteArray, iv: ByteArray, password: CharArray): ByteArray {
        val secretKey = deriveKey(password)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }
}
