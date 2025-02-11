package com.ricsdev.upass.util.Auth

import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.ricsdev.upass.presentation.home.AuthViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.launch
import uPass.composeApp.BuildConfig

val supabase = createSupabaseClient(
    supabaseUrl = "https://tpyvkwfbekpxklvdyvic.supabase.co",
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Auth) {
        // Configure your auth settings
    }
    install(ComposeAuth) {
        googleNativeLogin(serverClientId = "194042902859-77mio4bemb7br3ba93mafo1utlb7dmbp.apps.googleusercontent.com")

        appleNativeLogin()
    }
}


@OptIn(AuthUiExperimental::class)
@Composable
fun GoogleSignInButton(authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val action = supabase.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
//            authViewModel.onSignInResult(result)
            when (result) {
                is NativeSignInResult.Success -> {
                    println("Success")
                }

                is NativeSignInResult.ClosedByUser -> {
                    println("closed by user")
                }

                is NativeSignInResult.Error -> {
                    println("error")
                }

                is NativeSignInResult.NetworkError -> {
                    println("network error")
                }
            }
        }
    )
    OutlinedButton(
//        onClick = { action.startFlow() },
        onClick = {
            coroutineScope.launch {
                supabase.auth.signInWith(Google)
            }

        },
        content = { ProviderButtonContent(Google) }
    )
}