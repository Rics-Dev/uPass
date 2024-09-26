package com.ricsdev.uconnect.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.domain.model.Account
import com.ricsdev.uconnect.getPlatform
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
    val accountsState by viewModel.accountsState.collectAsState()
    var showPasswordGenerator by remember { mutableStateOf(false) }
    var show2faSetup by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

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
                title = { Text(text = "uConnect") }
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
                if (accounts.isEmpty()) {
                    EmptyStateMessage(modifier = Modifier.padding(innerPadding))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(accounts) { account ->
                            AccountCard(
                                account = account,
                                onShowSnackbar = { message -> snackbarMessage = message },
                                navHostController = navHostController
                            )
                        }
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
            Icon(Icons.Outlined.Shield, contentDescription = "No accounts",
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
    account: Account,
    onShowSnackbar: (String) -> Unit,
    navHostController: NavHostController
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val platform = getPlatform()

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(8.dp), clip = true),
        onClick = {
            navHostController.navigate(Screens.AccountDetailsScreen(account.id))
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
            Row {
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(account.username))
                    if (!platform.name.contains("Android")) {
                        onShowSnackbar("Username copied to clipboard")
                    }
                }) {
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
    }
}
