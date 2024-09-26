package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect

@Composable
actual fun LoginContent(
    viewModel: SetupViewModel,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
) {
    BindBiometryAuthenticatorEffect(viewModel.biometryAuthenticator as BiometryAuthenticator)
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(isBiometricEnabled) {
        if (isBiometricEnabled) {
            viewModel.loginWithBiometric()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp), // Ensure space for the biometric icon
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Password,
                contentDescription = "Password",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text("Enter Master Password", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = {
                    Text(
                        text = error ?: "Master Password",
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = error != null,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (password.isNotBlank()) {
                            keyboard?.hide()
                            onLogin()
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onLogin) {
                Text("Unlock")
            }
        }

        if(isBiometricEnabled){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                IconButton(
                    onClick = {
                        viewModel.loginWithBiometric()
                    }
                ) {
                    Icon(
                        Icons.Filled.Fingerprint,
                        contentDescription = "Biometric",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}

@Composable
actual fun BiometricScreen(
    viewModel: SetupViewModel,
    error: String?,
) {
    BindBiometryAuthenticatorEffect(viewModel.biometryAuthenticator as BiometryAuthenticator)
    var iconAlpha by remember { mutableFloatStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = iconAlpha,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    LaunchedEffect(Unit) {
        iconAlpha = 1f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp), // Ensure space for the biometric icon
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = "Welcome icon",
                modifier = Modifier
                    .size(64.dp)
                    .alpha(animatedAlpha)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Set up Biometric Authentication", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("(Recommended)", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Enable biometric authentication, you can skip this part.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (error != null) {
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ElevatedButton(
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.size(96.dp),
                onClick = {
                    viewModel.enableBiometrics()
                }
            ) {
                Icon(
                    Icons.Filled.Fingerprint,
                    contentDescription = "Biometric",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}