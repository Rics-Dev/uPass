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
import com.ricsdev.uconnect.util.SecureStorage
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SetupScreen(
    navController: NavHostController
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val secureStorage: SecureStorage = koinInject()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set up Master Password", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Master Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Master Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (password.length < 8) {
                    error = "Password must be at least 8 characters long"
                } else if (password != confirmPassword) {
                    error = "Passwords do not match"
                } else {
                    error = null
                    coroutineScope.launch {
                        secureStorage.setMasterPassword(password)
                        navController.navigate(Screens.HomeScreen) {
                            popUpTo(Screens.SetupScreen) { inclusive = true }
                        }
                    }
                }
            }
        ) {
            Text("Set Master Password")
        }
    }
}
