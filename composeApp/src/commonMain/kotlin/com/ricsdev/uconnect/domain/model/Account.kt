package com.ricsdev.uconnect.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Account(
    val id: Int = 0,
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val urls: List<String> = listOf(""),
    val twoFaSettings: TwoFaSettings? = TwoFaSettings(),
    val customFields: List<CustomField> = emptyList(),
    val note: String = ""
)

@Serializable
data class TwoFaSettings(
    val issuer: String = "",
    val accountName: String = "",
    val secret: String = "",
    val type: OtpType = OtpType.TOTP,
    val hmacAlgorithm: HmacAlgorithm = HmacAlgorithm.SHA1,
    val period: OtpPeriod = OtpPeriod.Thirty,
    val digits: OtpDigits = OtpDigits.Six,
    val counter: Long = 0 // Only used for HOTP
)

@Serializable
enum class HmacAlgorithm {
    SHA1, SHA256, SHA512
}

@Serializable
enum class OtpType {
    TOTP, HOTP
}

@Serializable
enum class OtpDigits(val number: Int) {
    Six(6), Eight(8)
}

@Serializable
enum class OtpPeriod(val seconds: Int) {
    Fifteen(15), Thirty(30), Sixty(60);

    val millis: Long
        get() = seconds * 1000L
}





enum class CustomFieldType {
    TEXT, HIDDEN, BOOLEAN, LINKED
}


@Serializable
data class CustomField(
    val type: CustomFieldType,
    val label: String,
    var value: String = ""
)
