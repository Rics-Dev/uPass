package com.ricsdev.uconnect.domain.model

data class Password(
    val value: String = "",
    val isPassphrase: Boolean = false,
    val length: Int = 8,
    val useUppercase: Boolean = true,
    val useLowercase: Boolean = true,
    val useNumbers: Boolean = true,
    val useSymbols: Boolean = true,
    val minNumbers: Int = 1,
    val minSymbols: Int = 1,
    val avoidAmbiguous: Boolean = false,
    val history: List<String> = emptyList()
)
