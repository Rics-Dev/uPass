package com.ricsdev.upass.presentation.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ricsdev.upass.PlatformType
import com.ricsdev.upass.domain.model.CustomField
import com.ricsdev.upass.domain.model.CustomFieldType
import com.ricsdev.upass.domain.model.TwoFaSettings
import com.ricsdev.upass.getPlatform
import com.ricsdev.upass.presentation.home.components.modal.HmacAlgorithmDropdown
import com.ricsdev.upass.presentation.home.components.modal.OtpDigitsDropdown
import com.ricsdev.upass.presentation.home.components.modal.OtpTypeDropdown
import com.ricsdev.upass.presentation.home.components.modal.TotpPeriodDropdown
import com.ricsdev.upass.presentation.sharedComponents.passwordGenerator.PasswordGenerator
import com.ricsdev.upass.util.twoFa.isValidBase32
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    navHostController: NavHostController
) {

    val viewModel = koinViewModel<AccountViewModel>()
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()
    var showPasswordGenerator by remember { mutableStateOf(false) }
    var show2faSetup by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }


    val keyboard = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = { Text("Add new account") },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            when {
                                accountState.name.isEmpty() -> {
                                    snackbarMessage = "Account name is required"
                                }

                                accountState.twoFaSettings?.secret?.isValidBase32 == false -> {
                                    snackbarMessage = "Invalid 2FA secret"
                                }

                                else -> {
                                    viewModel.saveAccount()
                                    navHostController.navigateUp()
                                    keyboard?.hide()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Save, contentDescription = "save account")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                AccountItem(
                    icon = Icons.Outlined.Public,
                    label = "Account",
                    value = accountState.name,
                    onValueChange = { viewModel.updateAccountState(accountState.copy(name = it)) },
                    keyboardType = KeyboardType.Text
                )
                Row(
                    modifier = Modifier.padding(horizontal = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when {
                            accountState.name.isEmpty() -> Icons.Outlined.Info
                            else -> Icons.Outlined.Check
                        },
                        contentDescription = "Info icon",
                        modifier = Modifier.size(14.dp),
                        tint = when {
                            accountState.name.isEmpty() -> MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )

                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(
                        "Required",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            accountState.name.isEmpty() -> MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )

                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
                AccountItem(
                    icon = Icons.Outlined.AccountCircle,
                    label = "Username / Email",
                    value = accountState.username,
                    onValueChange = { viewModel.updateAccountState(accountState.copy(username = it)) },
                    keyboardType = KeyboardType.Email
                )
                AccountItem(
                    icon = Icons.Outlined.Password,
                    label = "Password",
                    value = accountState.password,
                    onValueChange = { viewModel.updateAccountState(accountState.copy(password = it)) },
                    isPassword = true,
                    showPasswordGenerator = {
                        showPasswordGenerator = true
                    },
                    keyboardType = KeyboardType.Password
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))


                AccountItem(
                    icon = Icons.Outlined.Link,
                    label = "Url",
                    value = accountState.url,
                    onValueChange = { viewModel.updateAccountState(accountState.copy(url = it)) },
                    keyboardType = KeyboardType.Uri
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "2FA Setup",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    AnimatedVisibility(visible = show2faSetup) {
                        IconButton(
                            onClick = { show2faSetup = false },
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "Discard 2fa")
                        }
                    }
                }
                AnimatedVisibility(visible = !show2faSetup) {
                    Button(
                        onClick = { show2faSetup = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Add 2FA")
                    }
                }

                AnimatedVisibility(visible = show2faSetup) {
                    TwoFaSettingsSection(
                        twoFaSettings = accountState.twoFaSettings!!,
                        onTwoFaSettingsChange = {
                            viewModel.updateAccountState(
                                accountState.copy(
                                    twoFaSettings = it
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                CustomFieldsSection(
                    customFields = accountState.customFields,
                    onCustomFieldChange = { index, field ->
                        viewModel.updateCustomField(
                            index,
                            field
                        )
                    },
                    onAddCustomField = { viewModel.addCustomField() },
                    onRemoveCustomField = { index -> viewModel.removeCustomField(index) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NoteSection(
                    note = accountState.note,
                    onNoteChange = { viewModel.updateAccountState(accountState.copy(note = it)) }
                )

            }


        }
    }
    if (showPasswordGenerator) {
        PasswordGenerator(
            isAddingAccount = true,
            usePassword = { password ->
                viewModel.updateAccountState(accountState.copy(password = password))
                showPasswordGenerator = false

            },
            onDismiss = {
                showPasswordGenerator = false
            },
        )
    }
}


@Composable
fun TwoFaSettingsSection(
    twoFaSettings: TwoFaSettings,
    onTwoFaSettingsChange: (TwoFaSettings) -> Unit
) {
    var is2faSettingsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        AccountItem(
            icon = Icons.Outlined.Key,
            label = "Secret Key",
            value = twoFaSettings.secret,
            onValueChange = { onTwoFaSettingsChange(twoFaSettings.copy(secret = it)) },
            is2fa = true,
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(8.dp))

        ListItem(
            leadingContent = {
                Icon(Icons.Outlined.Settings, contentDescription = "Advanced 2FA settings")
            },
            headlineContent = { Text("Advanced settings") },
            trailingContent = {
                IconButton(onClick = { is2faSettingsExpanded = !is2faSettingsExpanded }) {
                    Icon(
                        if (is2faSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (is2faSettingsExpanded) "Collapse" else "Expand"
                    )
                }
            }
        )

        AnimatedVisibility(visible = is2faSettingsExpanded) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OtpTypeDropdown(
                        modifier = Modifier.weight(1f),
                        selectedType = twoFaSettings.type,
                        onTypeSelected = { onTwoFaSettingsChange(twoFaSettings.copy(type = it)) }
                    )
                    HmacAlgorithmDropdown(
                        modifier = Modifier.weight(1f),
                        selectedAlgorithm = twoFaSettings.hmacAlgorithm,
                        onAlgorithmSelected = {
                            onTwoFaSettingsChange(
                                twoFaSettings.copy(
                                    hmacAlgorithm = it
                                )
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TotpPeriodDropdown(
                        modifier = Modifier.weight(1f),
                        selectedPeriod = twoFaSettings.period,
                        onPeriodSelected = { onTwoFaSettingsChange(twoFaSettings.copy(period = it)) }
                    )
                    OtpDigitsDropdown(
                        modifier = Modifier.weight(1f),
                        selectedDigits = twoFaSettings.digits,
                        onDigitsSelected = { onTwoFaSettingsChange(twoFaSettings.copy(digits = it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomFieldsSection(
    customFields: List<CustomField>,
    onCustomFieldChange: (Int, CustomField) -> Unit,
    onAddCustomField: () -> Unit,
    onRemoveCustomField: (Int) -> Unit
) {
    Text(
        text = "Custom Fields",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    customFields.forEachIndexed { index, field ->
        CustomFieldInput(
            field = field,
            onFieldChange = { newField -> onCustomFieldChange(index, newField) },
            onRemoveField = { onRemoveCustomField(index) }
        )
    }
    Button(
        onClick = { onAddCustomField() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("Add Custom Field")
    }
}

@Composable
fun NoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Text(
        text = "Note",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("Note") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFieldInput(
    field: CustomField,
    onFieldChange: (CustomField) -> Unit,
    onRemoveField: () -> Unit
) {

    var expandedType by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expandedType,
            onExpandedChange = { expandedType = it }
        ) {
            OutlinedTextField(
                value = field.type.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable
                    )
            )
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                CustomFieldType.entries.forEach { type ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(type.name) },
                        onClick = {
                            onFieldChange(field.copy(type = type))
                            expandedType = false
                        }
                    )
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = field.label,
                onValueChange = { onFieldChange(field.copy(label = it)) },
                label = { Text("Label") },
                modifier = Modifier.weight(1f)
            )
            when (field.type) {
                CustomFieldType.TEXT, CustomFieldType.HIDDEN -> {
                    OutlinedTextField(
                        value = field.value,
                        onValueChange = { onFieldChange(field.copy(value = it)) },
                        label = { Text("Value") },
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (field.type == CustomFieldType.HIDDEN) PasswordVisualTransformation() else VisualTransformation.None
                    )
                }

                CustomFieldType.BOOLEAN -> {
                    Checkbox(
                        checked = field.value.toBoolean(),
                        onCheckedChange = { onFieldChange(field.copy(value = it.toString())) }
                    )
                }

                CustomFieldType.LINKED -> {
                    OutlinedTextField(
                        value = field.value,
                        onValueChange = { onFieldChange(field.copy(value = it)) },
                        label = { Text("URL") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            ElevatedButton(
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(48.dp),
                onClick = onRemoveField
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Remove Field")
            }
        }
    }
}

@Composable
fun UrlInputField(
    url: String,
    onUrlChange: (String) -> Unit,
    onRemoveUrl: () -> Unit,
    index: Int,
    urlListSize: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Outlined.Link, contentDescription = "Url")
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Url") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        if (index < urlListSize - 1) {
            ElevatedButton(
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(48.dp),
                onClick = onRemoveUrl
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Remove Url")
            }
        }
    }
}

@Composable
fun AccountItem(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    is2fa: Boolean = false,
    showPasswordGenerator: () -> Unit = {},
    keyboardType: KeyboardType,
    readOnly: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val platform = getPlatform()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = label)
        OutlinedTextField(
            trailingIcon = {
                if (isPassword || is2fa) {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                            )
                        }
                        if (isPassword) {
                            IconButton(onClick = { showPasswordGenerator() }) {
                                Icon(
                                    imageVector = Icons.Outlined.LockReset,
                                    contentDescription = "Generate Password"
                                )
                            }
                        } else {
                            if (platform.type == PlatformType.ANDROID) {
                                IconButton(onClick = { }) { //qr code scanner
                                    Icon(
                                        imageVector = Icons.Outlined.QrCodeScanner,
                                        contentDescription = "Generate Password"
                                    )
                                }
                            }
                        }
                    }
                }
            },
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
                .focusRequester(focusRequester),
            visualTransformation = if ((isPassword || is2fa) && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            readOnly = readOnly,
            maxLines = 1,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequester.requestFocus()
                }
            ),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFaTypeDropdown(
    modifier: Modifier,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("TOTP", "HOTP")

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = { },
            readOnly = true,
            label = { Text("2FA Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable
                )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashFunctionDropdown(
    modifier: Modifier,
    selectedFunction: String,
    onFunctionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val functions = listOf("SHA1", "SHA256", "SHA512")

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedFunction,
            onValueChange = { },
            readOnly = true,
            label = { Text("Hash Function") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable
                )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            functions.forEach { function ->
                DropdownMenuItem(
                    text = { Text(function) },
                    onClick = {
                        onFunctionSelected(function)
                        expanded = false
                    }
                )
            }
        }
    }
}