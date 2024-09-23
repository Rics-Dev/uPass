package com.ricsdev.uconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.navigation.Screens
import com.ricsdev.uconnect.presentation.home.components.HomeFloatingActionButton
import com.ricsdev.uconnect.presentation.sharedComponents.passwordGenerator.PasswordGenerator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val viewModel = koinViewModel<HomeViewModel>()
    var showPasswordGenerator by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = { Text(text = "uConnect") },
                navigationIcon = { /* TODO */ }
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                addAccount = {
                    navHostController.navigate(Screens.NewAccountScreen.route)
                },
                showPasswordGenerator = {
                    showPasswordGenerator = true
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Your content here
        }
    }

    if (showPasswordGenerator) {
        PasswordGenerator(
            onDismiss = {
                showPasswordGenerator = false
            },
        )
    }
}



