package com.ricsdev.uconnect.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ricsdev.uconnect.presentation.account.AccountDetailsScreen
import com.ricsdev.uconnect.presentation.account.NewAccountScreen
import com.ricsdev.uconnect.presentation.home.HomeScreen
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.flow.first
import org.koin.compose.getKoin
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<Any?>(null) }
    val secureStorage: SecureStorage = koinInject<SecureStorage>()

    LaunchedEffect(Unit) {
        startDestination = if (secureStorage.isMasterPasswordSet().first()) {
            Screens.HomeScreen
        } else {
            Screens.SetupScreen
        }
    }


    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable<Screens.HomeScreen> {
                HomeScreen(navController)
            }


            composable<Screens.NewAccountScreen>(
                enterTransition = {
                    slideInVertically(initialOffsetY = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
            ) {
                NewAccountScreen(navController)
            }


            composable<Screens.AccountDetailsScreen> {
                val args = it.toRoute<Screens.AccountDetailsScreen>()
                AccountDetailsScreen(navController, args.id)
            }
        }

    }
}