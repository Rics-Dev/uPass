package com.ricsdev.uconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.navigation.Screens
import com.ricsdev.uconnect.presentation.home.components.HomeFloatingActionButton
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController
) {

    val viewModel = koinViewModel<HomeViewModel>()


    var showAddAccountModal by remember { mutableStateOf(false) }


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
//                    showAddAccountModal = true
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


        }



//        if (showAddAccountModal) {
//            AccountModal(
//                onDismiss = { showAddAccountModal = false }
//            )
//        }

    }

}


