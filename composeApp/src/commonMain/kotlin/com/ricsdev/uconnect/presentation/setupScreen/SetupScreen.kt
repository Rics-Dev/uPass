package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.navigation.Screens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupScreen(
    navController: NavHostController
) {
    val viewModel = koinViewModel<SetupViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SetupUiState.NavigateToHome -> {
                navController.navigate(Screens.HomeScreen) {
                    popUpTo(Screens.SetupScreen) { inclusive = true }
                }
            }
            else -> {} // Do nothing for other states
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val currentState = uiState) {
                is SetupUiState.InitialSetup -> {
                    SetupContent(
                        password = password,
                        confirmPassword = confirmPassword,
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onSetPassword = { viewModel.setMasterPassword(password) },
                        error = currentState.error
                    )
                }
                is SetupUiState.Login -> {
                    LoginContent(
                        password = password,
                        onPasswordChange = { password = it },
                        onLogin = { viewModel.login(password) },
                        error = currentState.error
                    )
                }
                else -> {} // NavigateToHome is handled in LaunchedEffect
            }
        }
    }
}




@Composable
fun SetupContent(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSetPassword: () -> Unit,
    error: String?
) {
    Text("Set up Master Password", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Master Password") },
        visualTransformation = PasswordVisualTransformation()
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Confirm Master Password") },
        visualTransformation = PasswordVisualTransformation()
    )
    if (error != null) {
        Text(error, color = MaterialTheme.colorScheme.error)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onSetPassword) {
        Text("Set Master Password")
    }
}

@Composable
fun LoginContent(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?
) {
    Text("Enter Master Password", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Master Password") },
        visualTransformation = PasswordVisualTransformation()
    )
    if (error != null) {
        Text(error, color = MaterialTheme.colorScheme.error)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onLogin) {
        Text("Login")
    }
}