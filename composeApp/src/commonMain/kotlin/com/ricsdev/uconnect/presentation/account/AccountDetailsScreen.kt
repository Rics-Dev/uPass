package com.ricsdev.uconnect.presentation.account


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    navHostController: NavHostController,
    accountId: Int
) {
    val viewModel = koinViewModel<AccountViewModel>()
    val accountState by viewModel.accountState.collectAsState()

    LaunchedEffect(accountId) {
        viewModel.fetchAccountDetails(accountId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        accountState.let { account ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                AccountItem(
                    icon = Icons.Outlined.Public,
                    label = "Account",
                    value = account.name,
                    onValueChange = { /* No-op */ },
                    keyboardType = KeyboardType.Text,
                    readOnly = true
                )
                AccountItem(
                    icon = Icons.Outlined.AccountCircle,
                    label = "Username / Email",
                    value = account.username,
                    onValueChange = { /* No-op */ },
                    keyboardType = KeyboardType.Email,
                    readOnly = true
                )
                AccountItem(
                    icon = Icons.Outlined.Password,
                    label = "Password",
                    value = account.password,
                    onValueChange = { /* No-op */ },
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    readOnly = true
                )
            }
        }
    }
}