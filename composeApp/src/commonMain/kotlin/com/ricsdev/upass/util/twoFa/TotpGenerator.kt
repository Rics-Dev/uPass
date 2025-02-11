package com.ricsdev.upass.util.twoFa

import com.ricsdev.upass.domain.model.TwoFaSettings
import kotlin.math.floor


class TotpGenerator(
    secret: String,
    private val settings: TwoFaSettings,
) {
    private val hotpGenerator: HotpGenerator = HotpGenerator(secret, settings)

    private fun counter(timestamp: Long): Long {
        val millis = settings.period.millis
        return if (millis <= 0L) 0 else floor(timestamp.toDouble() / millis).toLong()
    }

    fun generate(timestamp: Long): String =
        hotpGenerator.generate(counter(timestamp))

    private fun timeslotStart(timestamp: Long): Long {
        val counter = counter(timestamp)
        val timeStepMillis = settings.period.millis.toDouble()
        return (counter * timeStepMillis).toLong()
    }

    fun timeslotLeft(timestamp: Long): Double {
        val diff = timestamp - timeslotStart(timestamp)
        return 1.0 - diff.toDouble() / settings.period.millis.toDouble()
    }
}