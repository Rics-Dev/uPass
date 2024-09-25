package com.ricsdev.uconnect.navigation

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