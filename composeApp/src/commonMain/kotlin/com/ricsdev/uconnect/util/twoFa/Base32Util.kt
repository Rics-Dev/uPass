package com.ricsdev.uconnect.util.twoFa

import io.matthewnelson.encoding.base32.Base32
import io.matthewnelson.encoding.core.Decoder.Companion.decodeToByteArray
import io.matthewnelson.encoding.core.EncodingException

val String.isValidBase32: Boolean
    get() = if (isBlank()) false else try {
        decodeBase32ToByteArray()
        true
    } catch (_: EncodingException) {
        false
    }

fun String.decodeBase32ToByteArray(): ByteArray =
    encodeToByteArray().decodeToByteArray(Base32.Default)