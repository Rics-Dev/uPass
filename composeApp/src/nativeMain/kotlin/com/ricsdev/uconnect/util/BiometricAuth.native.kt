package com.ricsdev.uconnect.util

actual class BiometricAuth actual constructor() {
    actual suspend fun authenticate(): Boolean {
        TODO("Not yet implemented")
    }


    actual fun isBiometricAvailable(): Boolean {
        return false
    }
}