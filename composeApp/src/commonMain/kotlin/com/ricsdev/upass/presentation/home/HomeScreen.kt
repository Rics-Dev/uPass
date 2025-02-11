package com.ricsdev.upass.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricsdev.upass.domain.model.Account
import com.ricsdev.upass.getPlatform
import com.ricsdev.upass.navigation.Screens
import com.ricsdev.upass.presentation.home.components.HomeFloatingActionButton
import com.ricsdev.upass.presentation.home.components.modal.TwoFaSetup
import com.ricsdev.upass.presentation.sharedComponents.passwordGenerator.PasswordGenerator
import com.ricsdev.upass.util.Auth.GoogleSignInButton
import com.ricsdev.upass.util.PlatformVerticalScrollbar
import com.ricsdev.upass.util.loadIcon
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import upass.composeapp.generated.resources.Res
import upass.composeapp.generated.resources.jetbrains

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val authViewModel = koinViewModel<AuthViewModel>()
    val accountsState by viewModel.accountsState.collectAsState()
    var filteredAccountState by remember { mutableStateOf<List<Account>>(emptyList()) }
    var showPasswordGenerator by remember { mutableStateOf(false) }
    var show2faSetup by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()


    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }


    val username by authViewModel.username.collectAsState()

    val filePickerLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("json")),
        mode = PickerMode.Single,
        title = "Pick a vault file"
    ) { file ->
        coroutineScope.launch {
            val jsonContent = file?.readBytes()?.decodeToString()
            if (jsonContent != null) {
                println("executing import")
                viewModel.importVault(jsonContent)
            }
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                navigationIcon = {
                    if (username != null) {
                        Text(text = username ?: "")
                    } else {
                        GoogleSignInButton(authViewModel)
                    }
                },
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                filteredAccountState =
                                    (accountsState as UiState.Success<List<Account>>).data.filter { account ->
                                        account.name.contains(
                                            searchText,
                                            ignoreCase = true
                                        )
                                    }
                            },
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(text = "uConnect")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) {
                            searchText = ""
                        }
                    }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {
                        filePickerLauncher.launch()
                    }) {
                        Icon(Icons.Outlined.ImportExport, contentDescription = "Import vault")
                    }
                }
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                addAccount = {
                    navHostController.navigate(Screens.NewAccountScreen)
                },
                showPasswordGenerator = {
                    showPasswordGenerator = true
                },
                show2faSetup = {
                    show2faSetup = true
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (accountsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val accounts = (accountsState as UiState.Success<List<Account>>).data
                filteredAccountState = (accountsState as UiState.Success<List<Account>>).data
                if (accounts.isEmpty()) {
                    EmptyStateMessage(modifier = Modifier.padding(innerPadding))
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val listState = rememberLazyListState()
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(filteredAccountState) { account ->
                                AccountCard(
                                    viewModel = viewModel,
                                    account = account,
                                    onShowSnackbar = { message -> snackbarMessage = message },
                                    navHostController = navHostController
                                )
                            }
                        }

                        PlatformVerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            scrollState = listState
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (accountsState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showPasswordGenerator) {
        PasswordGenerator(
            onDismiss = { showPasswordGenerator = false },
        )
    }

    if (show2faSetup) {
        TwoFaSetup(
            onDismiss = { show2faSetup = false },
        )
    }
}

@Composable
fun EmptyStateMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Shield, contentDescription = "No accounts",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No accounts added yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Tap the + button to add one!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
fun AccountCard(
    viewModel: HomeViewModel,
    account: Account,
    onShowSnackbar: (String) -> Unit,
    navHostController: NavHostController
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val platform = getPlatform()

    val otpMap by viewModel.otpMap.collectAsStateWithLifecycle()
    val remainingTimeMap by viewModel.remainingTimeMap.collectAsStateWithLifecycle()

    val currentOtp = otpMap[account.id]
    val remainingTime = remainingTimeMap[account.id] ?: 30
    var isOtpVisible by remember { mutableStateOf(false) }
    val otpColor =
        if (remainingTime <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    val circleColor =
        if (remainingTime <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    val spacedOtp = currentOtp?.chunked(3)?.joinToString("  ")

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp), clip = true),
        onClick = {
            navHostController.navigate(Screens.AccountDetailsScreen(account.id))
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = loadIcon(account.name) ?: painterResource(Res.drawable.jetbrains),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = account.username,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                Row {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(account.username))
                            if (!platform.name.contains("Android")) {
                                onShowSnackbar("Username copied to clipboard")
                            }
                        },
                    ) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Copy username")
                    }
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(account.password))
                        if (!platform.name.contains("Android")) {
                            onShowSnackbar("Password copied to clipboard")
                        }
                    }) {
                        Icon(Icons.Outlined.Password, contentDescription = "Copy password")
                    }
                }
            }
            if (account.twoFaSettings != null && currentOtp != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(40.dp)
                        ) {
                            val remainingFraction =
                                remainingTime / account.twoFaSettings.period.seconds.toFloat()
                            val animatedFraction by animateFloatAsState(targetValue = remainingFraction)

                            Canvas(modifier = Modifier.size(40.dp)) {
                                drawArc(
                                    color = circleColor,
                                    startAngle = -90f,
                                    sweepAngle = 360 * animatedFraction,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx())
                                )
                            }
                            Text(
                                text = "$remainingTime",
                                style = MaterialTheme.typography.bodyMedium,
                                color = otpColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedVisibility(visible = isOtpVisible) {
                            Text(
                                text = spacedOtp ?: currentOtp,
                                style = MaterialTheme.typography.titleLarge,
                                color = otpColor,
                            )
                        }
                        AnimatedVisibility(visible = !isOtpVisible) {
                            Text(
                                text = spacedOtp?.map { "* " }?.joinToString("")
                                    ?: currentOtp.map { "* " }.joinToString(""),
                                style = MaterialTheme.typography.titleLarge,
                                color = otpColor,
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = {
                            isOtpVisible = !isOtpVisible
                        }) {
                            Icon(
                                imageVector = if (isOtpVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (isOtpVisible) "Hide 2FA" else "Show 2FA",
                            )
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(currentOtp))
                            if (!platform.name.contains("Android")) {
                                onShowSnackbar("2FA code copied to clipboard")
                            }
                        }) {
                            Icon(Icons.Outlined.Key, contentDescription = "Copy 2fa")
                        }
                    }
                }
            }
        }
    }
}