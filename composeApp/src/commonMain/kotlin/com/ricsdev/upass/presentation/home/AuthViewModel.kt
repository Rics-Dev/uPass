package com.ricsdev.upass.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    fun onSignInResult(result: NativeSignInResult) {
        viewModelScope.launch {
            when (result) {
                is NativeSignInResult.Success -> {
                    println("AuthViewModel Sign in success: ${result.toString()}")
                    // Assuming the username is part of the result
                    _username.value = result.toString() // or any other user info
                }

                is NativeSignInResult.ClosedByUser -> {
                    println("AuthViewModel Sign in closed by user")
                    _username.value = null
                }

                is NativeSignInResult.Error -> {
                    println("AuthViewModel Sign in error: ${result.toString()}")
                    _username.value = null
                }

                is NativeSignInResult.NetworkError -> {
                    println("AuthViewModel Network error during sign in")
                    _username.value = null
                }
            }
        }
    }
}