package com.ricsdev.uconnect.util.twoFa

import com.ditchoom.buffer.PlatformBuffer
import com.ditchoom.buffer.allocate
import com.ricsdev.uconnect.domain.model.HmacAlgorithm
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.util.CryptoManager
import kotlinx.coroutines.runBlocking
import org.kotlincrypto.macs.hmac.sha1.HmacSHA1
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.and
import kotlin.math.pow


typealias HotpCounter = Long

fun HotpCounter?.isValid(): Boolean {
    return this != null && this >= 0
}





class HotpGenerator(
    secret: String,
    private val settings: TwoFaSettings,
    private val cryptoManager: CryptoManager
) {
    private val secret: ByteArray = secret.decodeBase32ToByteArray()

    fun generate(count: HotpCounter): String {
        val message = PlatformBuffer.allocate(8).apply { this[0] = count }

        val hash = when (settings.hmacAlgorithm) {
            HmacAlgorithm.SHA1 -> HmacSHA1(secret)
            HmacAlgorithm.SHA256 -> HmacSHA256(secret)
            HmacAlgorithm.SHA512 -> HmacSHA512(secret)
        }.doFinal(message.readByteArray(8))

        val offset = hash.last().and(0x0F).toInt()

        val binary = PlatformBuffer.allocate(4).apply {
            for (i in 0..3) {
                this[i] = hash[i + offset]
            }
        }

        binary[0] = binary[0].and(0x7F)

        val digits = settings.digits.number
        val codeInt = binary.readInt().rem(10.0.pow(digits).toInt())

        return codeInt.toString().padStart(digits, padChar = '0')
    }
}











//class HotpGenerator(
//    secret: String,
//    private val settings: TwoFaSettings,
//    private val cryptoManager: CryptoManager
//) {
//    private val secret: ByteArray = secret.decodeBase32ToByteArray()
//
//    suspend fun generate(counter: HotpCounter): String {
//        val hasher = when (settings.hmacAlgorithm) {
//            HmacAlgorithm.SHA1 -> cryptoManager.hmacSha1()
//            HmacAlgorithm.SHA256 -> cryptoManager.hmacSha256()
//            HmacAlgorithm.SHA512 -> cryptoManager.hmacSha512()
//        }
//
//        val counterBytes = ByteArray(8)
//        for (i in 7 downTo 0) {
//            counterBytes[i] = (counter ushr ((7 - i) * 8)).toByte()
//        }
//
//        val hash = hasher.hash(secret + counterBytes)
//        val offset = hash.last().and(0x0F).toInt()
//
//        val binary = PlatformBuffer.allocate(4).apply {
//            for (i in 0..3) {
//                this[i] = hash[i + offset]
//            }
//        }
//
//        binary[0] = binary[0].and(0x7F)
//
//        val digits = settings.digits.number
//        val codeInt = binary.readInt().rem(10.0.pow(digits).toInt())
//
//        return codeInt.toString().padStart(digits, padChar = '0')
//    }
//}



