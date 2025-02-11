package com.ricsdev.upass.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

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

    private val salt = generateOrRetrieveSalt()

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
        val derivedKey = deriveKey(password.toCharArray())
        sharedPreferences.edit()
            .putString("derived_key", Base64.getEncoder().encodeToString(derivedKey))
            .putBoolean("master_password_set", true)
            .apply()
    }

    actual suspend fun verifyMasterPassword(password: String): Boolean =
        withContext(Dispatchers.IO) {
            val storedKey = sharedPreferences.getString("derived_key", null)
            val inputKey = Base64.getEncoder().encodeToString(deriveKey(password.toCharArray()))
            storedKey == inputKey
        }

    private fun deriveKey(password: CharArray): ByteArray {
        val spec = PBEKeySpec(password, salt, 65536, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    private fun generateOrRetrieveSalt(): ByteArray {
        val storedSalt =
            sharedPreferences.getString("salt", null)?.let { Base64.getDecoder().decode(it) }
        return storedSalt ?: ByteArray(16).apply {
            SecureRandom().nextBytes(this)
            sharedPreferences.edit().putString("salt", Base64.getEncoder().encodeToString(this))
                .apply()
        }
    }

    actual suspend fun isMasterPasswordSet(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean("master_password_set", false)
    }

    private fun ByteArray.encodeBase64(): String =
        android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)

    private fun String.decodeBase64(): ByteArray =
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)

    // biometry setup
    actual suspend fun isBiometricEnabled(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean("biometric_enabled", false)
    }

    actual fun saveBiometricState(biometricState: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", biometricState).apply()
    }
}