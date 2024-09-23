package com.ricsdev.uconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ricsdev.uconnect.presentation.home.HomeScreen

@Composable
fun AppNavigation(
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ) {
        composable(
            route = Screens.HomeScreen.route
        ) {
            HomeScreen()
        }
    }
}