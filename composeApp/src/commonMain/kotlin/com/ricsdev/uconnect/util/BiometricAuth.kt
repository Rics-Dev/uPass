package com.ricsdev.uconnect.util

expect class BiometricAuth {
    fun isBiometricAvailable(): Boolean
    fun isBiometricEnabled(): Boolean
    fun enableBiometric()
    fun disableBiometric()
    fun loadBiometricState()
    fun saveBiometricState()
}