package com.ricsdev.upass.navigation

import kotlinx.serialization.Serializable


sealed class Screens {
    @Serializable
    object SetupScreen


    @Serializable
    object HomeScreen


    @Serializable
    object NewAccountScreen


    @Serializable
    data class AccountDetailsScreen(val id: Int)
}