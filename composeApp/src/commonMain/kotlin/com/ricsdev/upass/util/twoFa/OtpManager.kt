package com.ricsdev.upass.util.twoFa

import com.ricsdev.upass.domain.model.OtpType
import com.ricsdev.upass.domain.model.TwoFaSettings


class OtpManager {
    fun generateOtp(settings: TwoFaSettings, timestamp: Long): String {
        return when (settings.type) {
            OtpType.TOTP -> {
                val generator = TotpGenerator(settings.secret, settings)
                generator.generate(timestamp)
            }

            OtpType.HOTP -> {
                val generator = HotpGenerator(settings.secret, settings)
                generator.generate(settings.counter)
            }
        }
    }

    fun timeslotLeft(settings: TwoFaSettings, timestamp: Long): Double {
        return if (settings.type == OtpType.TOTP) {
            val generator = TotpGenerator(settings.secret, settings)
            generator.timeslotLeft(timestamp)
        } else {
            1.0
        }
    }
}