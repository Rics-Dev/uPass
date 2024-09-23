package com.ricsdev.uconnect.domain.model

data class Account(
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val urls: List<String> = listOf(""),
    val twoFaSettings: TwoFaSettings? = null,
    val customFields: List<CustomField> = emptyList(),
    val note: String = ""
)

data class TwoFaSettings(
    val secretKey: String = "",
    val type: String = "TOTP",
    val hashFunction: String = "SHA1",
    val period: String = "30",
    val digits: String = "6"
)

enum class CustomFieldType {
    TEXT, HIDDEN, BOOLEAN, LINKED
}

data class CustomField(
    val type: CustomFieldType,
    val label: String,
    var value: String = ""
)
