package com.ricsdev.uconnect.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ricsdev.uconnect.presentation.account.AccountDetailsScreen
import com.ricsdev.uconnect.presentation.account.NewAccountScreen
import com.ricsdev.uconnect.presentation.home.HomeScreen
import com.ricsdev.uconnect.presentation.setupScreen.SetupScreen

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()



    NavHost(
        navController = navController,
        startDestination = Screens.SetupScreen
    ) {

        composable<Screens.SetupScreen> {
            SetupScreen(navController)
        }


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