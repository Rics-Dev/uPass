package com.ricsdev.uconnect.navigation


sealed class Screens(val route: String) {

    data object HomeScreen: Screens("home_screen")
    data object NewAccountScreen: Screens("new_account_screen")


}