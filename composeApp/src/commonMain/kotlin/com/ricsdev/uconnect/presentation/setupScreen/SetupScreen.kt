package com.ricsdev.uconnect.presentation.setupScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.Platform
import com.ricsdev.uconnect.PlatformType
import com.ricsdev.uconnect.getPlatform
import com.ricsdev.uconnect.navigation.Screens
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

enum class SetupStep {
    WELCOME, MASTER_PASSWORD, BIOMETRIC
}

@Composable
fun SetupScreen(
    navController: NavHostController
) {
    val viewModel = koinViewModel<SetupViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(SetupStep.WELCOME) }
    val platform = getPlatform()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(uiState) {
        when (uiState) {
            is SetupUiState.NavigateToHome -> {
                navController.navigate(Screens.HomeScreen) {
                    popUpTo(Screens.SetupScreen) { inclusive = true }
                }
            }

            is SetupUiState.InitialSetup -> {
                if ((uiState as SetupUiState.InitialSetup).isBiometricEnabled) {
                    coroutineScope.launch {
                        if (viewModel.setMasterPassword(password, confirmPassword)) {
                            navController.navigate(Screens.HomeScreen) {
                                popUpTo(Screens.SetupScreen) { inclusive = true }

                            }
                        }
                    }
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
                is SetupUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is SetupUiState.InitialSetup -> {
                    SetupContent(
                        viewModel = viewModel,
                        platform = platform,
                        password = password,
                        confirmPassword = confirmPassword,
                        currentStep = currentStep,
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onSetPassword = {
                            coroutineScope.launch {
                                if (viewModel.setMasterPassword(password, confirmPassword)) {
                                    navController.navigate(Screens.HomeScreen) {
                                        popUpTo(Screens.SetupScreen) { inclusive = true }

                                    }
                                }
                            }
                        },
                        onStepChange = { currentStep = it },
                        error = currentState.error,
                    )
                }

                is SetupUiState.Login -> {
                    LoginContent(
                        viewModel = viewModel,
                        password = password,
                        onPasswordChange = { password = it },
                        onLogin = { viewModel.login(password) },
                        error = currentState.error,
                        isBiometricEnabled = currentState.isBiometricEnabled
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
expect fun LoginContent(
    viewModel: SetupViewModel,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    error: String?,
    isBiometricEnabled: Boolean
)


@Composable
fun SetupContent(
    viewModel: SetupViewModel,
    platform: Platform,
    password: String,
    confirmPassword: String,
    currentStep: SetupStep,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSetPassword: () -> Unit,
    onStepChange: (SetupStep) -> Unit,
    error: String?,
) {

    var passwordError by remember { mutableStateOf<String?>(null) }
    val steps =
        if (platform.type == PlatformType.ANDROID) SetupStep.entries else SetupStep.entries.filter { it != SetupStep.BIOMETRIC }
    val pagerState = rememberPagerState(pageCount = { steps.size })

    // Check if the password conditions are met
    val passwordConditionsMet =
        passwordError.isNullOrBlank() && password.isNotBlank() && confirmPassword.isNotBlank()


    LaunchedEffect(currentStep) {
        pagerState.animateScrollToPage(currentStep.ordinal)
    }

    LaunchedEffect(pagerState.currentPage) {
        onStepChange(SetupStep.entries[pagerState.currentPage])
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)

                        do {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)

                            val currentPageOffsetFraction = pagerState.currentPageOffsetFraction
                            val isForwardScroll = currentPageOffsetFraction > 0

                            if (pagerState.currentPage == 1 && !passwordConditionsMet && isForwardScroll) {
                                event.changes.forEach { it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (SetupStep.entries[page]) {
                    SetupStep.WELCOME -> WelcomeScreen()
                    SetupStep.MASTER_PASSWORD -> MasterPasswordScreen(
                        password = password,
                        confirmPassword = confirmPassword,
                        onPasswordChange = onPasswordChange,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        passwordError = passwordError,
                        setPasswordError = { error -> passwordError = error }
                    )
                    SetupStep.BIOMETRIC -> BiometricScreen(
                        viewModel = viewModel,
                        error = error,
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                SetupStep.entries.forEachIndexed { index, step ->
                    if (platform.type == PlatformType.ANDROID || index < 2) {
                        StepDot(isSelected = pagerState.currentPage == index)
                        if (step != SetupStep.entries.last()) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = if (pagerState.currentPage != 0) Arrangement.SpaceBetween else Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = pagerState.currentPage != 0,
                ) {
                    TextButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                onStepChange(SetupStep.entries[pagerState.currentPage - 1])
                            }
                        }
                    ) {
                        Text("Back")
                    }
                }


                //Next / finish
                Button(
                    enabled = pagerState.currentPage != 1 || (passwordError.isNullOrBlank() && password.isNotBlank() && confirmPassword.isNotBlank()),
                    onClick = {
                        when {
                            pagerState.currentPage < pagerState.pageCount - 1 -> {
                                onStepChange(SetupStep.entries[pagerState.currentPage + 1])
                            }

                            platform.type == PlatformType.DESKTOP -> onSetPassword()
                            else -> onSetPassword()
                        }
                    }
                ) {
                    Text(
                        when {
                            pagerState.currentPage == pagerState.pageCount - 1 -> "Finish"
                            pagerState.currentPage == 1 && platform.type != PlatformType.ANDROID -> "Finish"
                            else -> "Next"
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun WelcomeScreen() {

    var iconAlpha by remember { mutableStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = iconAlpha,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        iconAlpha = 1f
    }


    Icon(
        Icons.Outlined.Shield,
        contentDescription = "Welcome icon",
        modifier = Modifier.size(64.dp)
            .alpha(animatedAlpha)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text("Welcome to uConnect", style = MaterialTheme.typography.headlineMedium)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "UConnect helps you manage your passwords and your 2FAs securely. " +
                "Let's set up your account in a few simple steps.",
        textAlign = TextAlign.Center
    )
}

@Composable
fun MasterPasswordScreen(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    passwordError: String?,
    setPasswordError: (String?) -> Unit,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var iconAlpha by remember { mutableStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = iconAlpha,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        iconAlpha = 1f
    }

    val keyboard = LocalSoftwareKeyboardController.current
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    fun validatePassword(password: String, confirmPassword: String) {
        when {
            password.length < 8 -> setPasswordError("Password must be at least 8 characters long")
            password != confirmPassword -> setPasswordError("Passwords do not match")
            else -> setPasswordError(null)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            Icons.Outlined.Password,
            contentDescription = "Welcome icon",
            modifier = Modifier
                .size(64.dp)
                .alpha(animatedAlpha)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Set up a Master Password", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Please enter and confirm your master password. This will be used to secure your account.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
            },
            value = password,
            onValueChange = {
                onPasswordChange(it)
                validatePassword(it, confirmPassword)
            },
            label = { Text(text = "Master Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordError != null && passwordError != "Passwords do not match",
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    confirmPasswordFocusRequester.requestFocus()
                }
            ),
            modifier = Modifier.focusRequester(confirmPasswordFocusRequester)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = when {
                    password.isEmpty() -> Icons.Outlined.Info
                    password.length < 8 -> Icons.Outlined.Info
                    password.length >= 8 -> Icons.Rounded.Check
                    else -> Icons.Outlined.Info
                },
                contentDescription = "Info icon",
                modifier = Modifier.size(14.dp),
                tint = when {
                    password.isEmpty() -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    password.length < 8 -> MaterialTheme.colorScheme.error
                    password.length >= 8 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
            Text(
                "Password must be at least 8 characters long",
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    password.isEmpty() -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    password.length < 8 -> MaterialTheme.colorScheme.error
                    password.length >= 8 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            trailingIcon = {
                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (isConfirmPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
            },
            value = confirmPassword,
            onValueChange = {
                onConfirmPasswordChange(it)
                validatePassword(password, it)
            },
            label = { Text(text = if (passwordError == "Passwords do not match") passwordError else "Confirm Master Password") },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            maxLines = 1,
            isError = passwordError != null && passwordError != "Password must be at least 8 characters long",
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (password.isNotBlank()) {
                        keyboard?.hide()
                    }
                }
            ),
            modifier = Modifier.focusRequester(confirmPasswordFocusRequester)
        )
    }
}

@Composable
expect fun BiometricScreen(
    viewModel: SetupViewModel,
    error: String?,
)

@Composable
fun StepDot(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                shape = CircleShape
            )
    )
}

