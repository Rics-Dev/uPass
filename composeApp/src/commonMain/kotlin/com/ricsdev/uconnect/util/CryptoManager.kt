package com.ricsdev.uconnect.util

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.digest.SHA512
import dev.whyoleg.cryptography.algorithms.symmetric.AES
import dev.whyoleg.cryptography.algorithms.asymmetric.ECDSA
import dev.whyoleg.cryptography.algorithms.asymmetric.EC
import dev.whyoleg.cryptography.algorithms.symmetric.SymmetricKeySize
import kotlinx.coroutines.flow.Flow


//expect class SecureStorage {
//    suspend fun saveKey(key: ByteArray)
//    suspend fun retrieveKey(): ByteArray?
//}

expect class SecureStorage {
    suspend fun saveKey(key: ByteArray)
    suspend fun retrieveKey(): ByteArray?
    suspend fun setMasterPassword(password: String)
    suspend fun verifyMasterPassword(password: String): Boolean
    fun isMasterPasswordSet(): Flow<Boolean>
//    suspend fun reset()
}


class CryptoManager(private val secureStorage: SecureStorage) {
    private val provider = CryptographyProvider.Default

    // SHA-512
    suspend fun hash(input: ByteArray): ByteArray {
        return try {
            val hasher = provider.get(SHA512).hasher()
            hasher.hash(input)
        } catch (e: Exception) {
            // Handle exception
            ByteArray(0)
        }
    }

    // AES-GCM
    private lateinit var aesKey: AES.GCM.Key
    private val aesGcm = provider.get(AES.GCM)

    suspend fun initializeAesKey() {
        try {
            val existingKey = secureStorage.retrieveKey()
            aesKey = if (existingKey != null) {
                aesGcm.keyDecoder().decodeFrom(AES.Key.Format.RAW, existingKey)
            } else {
                val newKey = aesGcm.keyGenerator(SymmetricKeySize.B256).generateKey()
                secureStorage.saveKey(newKey.encodeTo(AES.Key.Format.RAW))
                newKey
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    suspend fun encrypt(plaintext: ByteArray): ByteArray {
        return try {
            val cipher = aesKey.cipher()
            cipher.encrypt(plaintext)
        } catch (e: Exception) {
            // Handle exception
            ByteArray(0)
        }
    }

    suspend fun decrypt(ciphertext: ByteArray): ByteArray {
        return try {
            val cipher = aesKey.cipher()
            cipher.decrypt(ciphertext)
        } catch (e: Exception) {
            // Handle exception
            ByteArray(0)
        }
    }

    // ECDSA
    private lateinit var ecdsaKeyPair: ECDSA.KeyPair
    private val ecdsa = provider.get(ECDSA)

    suspend fun initializeEcdsaKeyPair() {
        try {
            ecdsaKeyPair = ecdsa.keyPairGenerator(EC.Curve.P521).generateKey()
        } catch (e: Exception) {
            // Handle exception
        }
    }

    suspend fun sign(data: ByteArray): ByteArray {
        return try {
            val signatureGenerator = ecdsaKeyPair.privateKey.signatureGenerator(digest = SHA512)
            signatureGenerator.generateSignature(data)
        } catch (e: Exception) {
            // Handle exception
            ByteArray(0)
        }
    }

    suspend fun verify(data: ByteArray, signature: ByteArray): Boolean {
        return try {
            val signatureVerifier = ecdsaKeyPair.publicKey.signatureVerifier(digest = SHA512)
            signatureVerifier.verifySignature(data, signature)
        } catch (e: Exception) {
            // Handle exception
            false
        }
    }
}