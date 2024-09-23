package com.ricsdev.uconnect.presentation.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ricsdev.uconnect.presentation.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

data class Account(
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val urls: List<String> = listOf(""),
    val twoFaSettings: TwoFaSettings? = null,
    val customFields: List<CustomField> = emptyList(),
    val note: String = ""
)

data class TwoFaSettings(
    val secretKey: String = "",
    val type: String = "TOTP",
    val hashFunction: String = "SHA1",
    val period: String = "30",
    val digits: String = "6"
)

enum class CustomFieldType {
    TEXT, HIDDEN, BOOLEAN, LINKED
}

data class CustomField(
    val type: CustomFieldType,
    val label: String,
    var value: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAccountScreen(
    navHostController: NavHostController
//    onDismiss: () -> Unit,
) {

    val viewModel = koinViewModel<AccountViewModel>()


//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true,
//        confirmValueChange = {
//            it != SheetValue.PartiallyExpanded
//        }
//    )

    val uiState by viewModel.uiState.collectAsState()

//    ModalBottomSheet(
//        shape = BottomSheetDefaults.HiddenShape,
//        dragHandle = null,
//        onDismissRequest = {
//            onDismiss()
//        },
//        sheetState = sheetState,
//    ) {
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
                            onClick = { viewModel.saveAccount() }
                        ){
                            Icon(Icons.Outlined.Save, contentDescription = "save account")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f) // Give the scrollable content as much space as possible
                        .verticalScroll(rememberScrollState())
                ) {
                    AccountItem(
                        icon = Icons.Outlined.Public,
                        label = "Account",
                        value = uiState.account.name,
                        onValueChange = { viewModel.updateAccountName(it) }
                    )
                    AccountItem(
                        icon = Icons.Outlined.AccountCircle,
                        label = "Username / Email",
                        value = uiState.account.username,
                        onValueChange = { viewModel.updateUsername(it) }
                    )
                    AccountItem(
                        icon = Icons.Outlined.Password,
                        label = "Password",
                        value = uiState.account.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        isPassword = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.account.urls.forEachIndexed { index, url ->
                        UrlInputField(
                            url = url,
                            onUrlChange = { newUrl -> viewModel.updateUrl(index, newUrl) },
                            onRemoveUrl = { viewModel.removeUrl(index) },
                            index = index,
                            urlListSize = uiState.account.urls.size
                        )
                    }
                    Button(
                        onClick = { viewModel.addUrl() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Add another URL")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "2FA Setup",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    AnimatedVisibility(visible = uiState.account.twoFaSettings == null) {
                        Button(
                            onClick = { viewModel.initializeTwoFaSettings() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Add 2FA")
                        }
                    }

                    AnimatedVisibility(visible = uiState.account.twoFaSettings != null) {
                        TwoFaSettingsSection(
                            twoFaSettings = uiState.account.twoFaSettings!!,
                            onTwoFaSettingsChange = { viewModel.updateTwoFaSettings(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    CustomFieldsSection(
                        customFields = uiState.account.customFields,
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
                        note = uiState.account.note,
                        onNoteChange = { viewModel.updateNote(it) }
                    )

                }


            }
        }
//    }
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
            value = twoFaSettings.secretKey,
            onValueChange = { onTwoFaSettingsChange(twoFaSettings.copy(secretKey = it)) },
            is2fa = true
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
                    TwoFaTypeDropdown(
                        modifier = Modifier.weight(1f),
                        selectedType = twoFaSettings.type,
                        onTypeSelected = { onTwoFaSettingsChange(twoFaSettings.copy(type = it)) }
                    )
                    HashFunctionDropdown(
                        modifier = Modifier.weight(1f),
                        selectedFunction = twoFaSettings.hashFunction,
                        onFunctionSelected = { onTwoFaSettingsChange(twoFaSettings.copy(hashFunction = it)) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = twoFaSettings.period,
                        onValueChange = { onTwoFaSettingsChange(twoFaSettings.copy(period = it)) },
                        label = { Text("Period (seconds)") }
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = twoFaSettings.digits,
                        onValueChange = { onTwoFaSettingsChange(twoFaSettings.copy(digits = it)) },
                        label = { Text("Digits") }
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
                    .menuAnchor()
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
            IconButton(onClick = onRemoveField) {
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
    is2fa: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = if (isPassword) Icons.Outlined.LockReset else Icons.Outlined.QrCodeScanner,
                                contentDescription = "Generate Password"
                            )
                        }
                    }
                }
            },
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if ((isPassword || is2fa) && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None
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
                .menuAnchor()
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
                .menuAnchor()
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