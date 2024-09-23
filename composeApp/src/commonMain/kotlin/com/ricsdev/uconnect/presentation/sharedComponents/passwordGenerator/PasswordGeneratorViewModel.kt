package com.ricsdev.uconnect.presentation.sharedComponents.passwordGenerator

import androidx.lifecycle.ViewModel
import com.ricsdev.uconnect.domain.model.Password
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random


class PasswordGeneratorViewModel : ViewModel() {

    private val _passwordState = MutableStateFlow(Password())
    val passwordState = _passwordState.asStateFlow()

    init {
        generatePassword()
    }

    fun updatePasswordState(update: Password.() -> Password) {
        _passwordState.value = _passwordState.value.update()
    }

    fun generatePassword() {
        val state = _passwordState.value
        val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
        val numberChars = "0123456789"
        val symbolChars = "!@#$%^&*"
        val ambiguousChars = "Il1O0"

        var chars = ""
        if (state.useUppercase) chars += uppercaseChars
        if (state.useLowercase) chars += lowercaseChars
        if (state.useNumbers) chars += numberChars
        if (state.useSymbols) chars += symbolChars
        if (state.avoidAmbiguous) chars = chars.filter { it !in ambiguousChars }

        if (chars.isEmpty()) {
            updatePasswordState { copy(value = "Error: No character sets selected") }
            return
        }

        if (state.isPassphrase) {
            updatePasswordState { copy(value = "passphrase-not-implemented") }
        } else {
            val password = StringBuilder()
            repeat(state.length) {
                password.append(chars.random())
            }

            // Ensure minimum numbers and symbols
            if (state.useNumbers) {
                repeat(state.minNumbers - password.count { it in numberChars }) {
                    password[Random.nextInt(state.length)] = numberChars.random()
                }
            }
            if (state.useSymbols) {
                repeat(state.minSymbols - password.count { it in symbolChars }) {
                    password[Random.nextInt(state.length)] = symbolChars.random()
                }
            }

            updatePasswordState { copy(value = password.toString(), history = (history + password.toString()).takeLast(10)) }
        }
    }
}


