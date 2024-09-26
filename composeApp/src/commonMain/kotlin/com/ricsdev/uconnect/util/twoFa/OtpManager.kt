package com.ricsdev.uconnect.util.twoFa

import com.ricsdev.uconnect.domain.model.OtpType
import com.ricsdev.uconnect.domain.model.TwoFaSettings
import com.ricsdev.uconnect.util.CryptoManager
import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.base64.Base64
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import kotlinx.datetime.Clock
import kotlin.math.floor


class OtpManager(private val cryptoManager: CryptoManager) {
    fun generateOtp(settings: TwoFaSettings, timestamp: Long): String {
        return when (settings.type) {
            OtpType.TOTP -> {
                val generator = TotpGenerator(settings.secret, settings, cryptoManager)
                generator.generate(timestamp)
            }
            OtpType.HOTP -> {
                val generator = HotpGenerator(settings.secret, settings, cryptoManager)
                generator.generate(settings.counter)
            }
        }
    }

    fun timeslotLeft(settings: TwoFaSettings, timestamp: Long): Double {
        return if (settings.type == OtpType.TOTP) {
            val generator = TotpGenerator(settings.secret, settings, cryptoManager)
            generator.timeslotLeft(timestamp)
        } else {
            1.0
        }
    }
}