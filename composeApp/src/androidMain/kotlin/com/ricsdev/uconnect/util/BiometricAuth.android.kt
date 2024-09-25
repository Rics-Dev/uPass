package com.ricsdev.uconnect.util

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager


actual class BiometricAuth(private val context: Context) {

    private var biometricEnabled = false
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)

    init {
        loadBiometricState()
    }

    actual fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    actual fun isBiometricEnabled(): Boolean = biometricEnabled

    actual fun enableBiometric() {
        biometricEnabled = true
        saveBiometricState()
    }

    actual fun disableBiometric() {
        biometricEnabled = false
        saveBiometricState()
    }

    actual fun loadBiometricState() {
        biometricEnabled = sharedPreferences.getBoolean("biometric_enabled", false)
    }

    actual fun saveBiometricState() {
        sharedPreferences.edit().putBoolean("biometric_enabled", biometricEnabled).apply()
    }

}