package com.ricsdev.upass

import androidx.compose.runtime.Composable
import com.ricsdev.upass.navigation.AppNavigation
import com.ricsdev.upass.ui.theme.UConnectTheme
import org.koin.compose.KoinContext


@Composable
fun App() {
    UConnectTheme {
        KoinContext {
            AppNavigation()
        }
    }
}