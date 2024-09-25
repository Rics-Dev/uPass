package com.ricsdev.uconnect.util

actual class BiometricAuth {



    actual fun isBiometricAvailable(): Boolean {
        return false
    }

    actual fun isBiometricEnabled(): Boolean {
        return false
    }

    actual fun enableBiometric() {
    }

    actual fun disableBiometric() {
    }

    actual fun loadBiometricState() {
    }

    actual fun saveBiometricState() {
    }
}