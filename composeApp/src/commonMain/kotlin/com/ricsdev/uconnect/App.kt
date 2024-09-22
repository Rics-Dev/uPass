package com.ricsdev.uconnect

import androidx.compose.runtime.Composable
import com.ricsdev.uconnect.navigation.AppNavigation
import com.ricsdev.uconnect.ui.theme.UConnectTheme
import org.koin.compose.KoinContext


@Composable
fun App() {
    UConnectTheme {
        KoinContext {
            AppNavigation()
        }
    }

}