package com.ricsdev.upass.util

import java.util.prefs.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

actual class SecureStorage {
    private val preferences = Preferences.userNodeForPackage(SecureStorage::class.java)
    private val salt = generateOrRetrieveSalt()
    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val derivedKey = retrieveDerivedKey()
                ?: throw IllegalStateException("Master password not set")
            val (encryptedKey, iv) = encrypt(key, derivedKey)
            preferences.putByteArray("encrypted_key", encryptedKey)
            preferences.putByteArray("iv", iv)
            preferences.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val derivedKey = retrieveDerivedKey()
                ?: throw IllegalStateException("Master password not set")
            val encryptedKey = preferences.getByteArray("encrypted_key", null)
            val iv = preferences.getByteArray("iv", null)
            if (encryptedKey != null && iv != null) {
                decrypt(encryptedKey, iv, derivedKey)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual suspend fun setMasterPassword(password: String) = withContext(Dispatchers.IO) {
        val derivedKey = deriveKey(password.toCharArray())
        val verificationHash = hashKeyForVerification(derivedKey)
        preferences.putByteArray("verification_hash", verificationHash)
        preferences.flush()
    }

    actual suspend fun verifyMasterPassword(password: String): Boolean =
        withContext(Dispatchers.IO) {
            val storedHash = preferences.getByteArray("verification_hash", null)
            if (storedHash != null) {
                val derivedKey = deriveKey(password.toCharArray())
                val inputHash = hashKeyForVerification(derivedKey)
                inputHash.contentEquals(storedHash)
            } else {
                false
            }
        }

    actual suspend fun isMasterPasswordSet(): Boolean = withContext(Dispatchers.IO) {
        (preferences.getByteArray("verification_hash", null) != null)
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

    private fun deriveKey(password: CharArray): ByteArray {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
        return factory.generateSecret(spec).encoded
    }

    private fun hashKeyForVerification(key: ByteArray): ByteArray {
        // Apply a hashing function for verification storage
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(key)
    }

    private fun retrieveDerivedKey(): ByteArray? {
        return preferences.getByteArray("derived_key", null)
    }

//    private fun encrypt(data: ByteArray, key: ByteArray): Pair<ByteArray, ByteArray> {
//        val secretKey = SecretKeySpec(key, "AES")
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        val iv = cipher.iv
//        val encryptedData = cipher.doFinal(data)
//        return Pair(encryptedData, iv)
//    }

    private fun encrypt(data: ByteArray, key: ByteArray): Pair<ByteArray, ByteArray> {
        val secretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12) // GCM standard IV size is 12 bytes
        SecureRandom().nextBytes(iv) // Generate a new random IV for each encryption
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val encryptedData = cipher.doFinal(data)
        return Pair(encryptedData, iv)
    }

    private fun decrypt(encryptedData: ByteArray, iv: ByteArray, key: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }


    // biometry setup
    actual suspend fun isBiometricEnabled(): Boolean = withContext(Dispatchers.IO) {
        false
    }

    actual fun saveBiometricState(biometricState: Boolean) {
    }
}

//    actual suspend fun reset() = withContext(Dispatchers.IO) {
//        try {
//            preferences.clear()
//            preferences.flush()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    actual suspend fun saveKey(key: ByteArray) = withContext(Dispatchers.IO) {
//        try {
//            val masterPassword = preferences.get("master_password", null)
//                ?: throw IllegalStateException("Master password not set")
//            val (encryptedKey, iv) = encrypt(key, masterPassword.toCharArray())
//            preferences.putByteArray("encrypted_key", encryptedKey)
//            preferences.putByteArray("iv", iv)
//            preferences.flush()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    actual suspend fun retrieveKey(): ByteArray? = withContext(Dispatchers.IO) {
//        try {
//            val masterPassword = preferences.get("master_password", null)
//                ?: throw IllegalStateException("Master password not set")
//            val encryptedKey = preferences.getByteArray("encrypted_key", null)
//            val iv = preferences.getByteArray("iv", null)
//            if (encryptedKey != null && iv != null) {
//                decrypt(encryptedKey, iv, masterPassword.toCharArray())
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    actual suspend fun setMasterPassword(password: String) = withContext(Dispatchers.IO) {
//        val hashedPassword = hashPassword(password)
//        preferences.put("master_password", hashedPassword)
//        preferences.flush()
//    }
//
//    actual suspend fun verifyMasterPassword(password: String): Boolean = withContext(Dispatchers.IO) {
//        val storedHash = preferences.get("master_password", null)
//        if (storedHash != null) {
//            val inputHash = hashPassword(password)
//            inputHash == storedHash
//        } else {
//            false
//        }
//    }
//
//
//    actual suspend fun isMasterPasswordSet(): Boolean = withContext(Dispatchers.IO) {
//        (preferences.get("master_password", null) != null)
//    }
//
//
//
//    private fun generateOrRetrieveSalt(): ByteArray {
//        val storedSalt = preferences.getByteArray("salt", null)
//        return if (storedSalt != null) {
//            storedSalt
//        } else {
//            val newSalt = ByteArray(16)
//            SecureRandom().nextBytes(newSalt)
//            preferences.putByteArray("salt", newSalt)
//            preferences.flush()
//            newSalt
//        }
//    }
//
//    private fun hashPassword(password: String): String {
//        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
//        val hash = factory.generateSecret(spec).encoded
//        return Base64.getEncoder().encodeToString(hash)
//    }
//
//    private fun deriveKey(password: CharArray): SecretKey {
//        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//        val spec: KeySpec = PBEKeySpec(password, salt, 65536, 256)
//        val secretKey = factory.generateSecret(spec)
//        return SecretKeySpec(secretKey.encoded, "AES")
//    }
//
//    private fun encrypt(data: ByteArray, password: CharArray): Pair<ByteArray, ByteArray> {
//        val secretKey = deriveKey(password)
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        val iv = cipher.iv
//        val encryptedData = cipher.doFinal(data)
//        return Pair(encryptedData, iv)
//    }
//
//    private fun decrypt(encryptedData: ByteArray, iv: ByteArray, password: CharArray): ByteArray {
//        val secretKey = deriveKey(password)
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//        val spec = GCMParameterSpec(128, iv)
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
//        return cipher.doFinal(encryptedData)
//    }
