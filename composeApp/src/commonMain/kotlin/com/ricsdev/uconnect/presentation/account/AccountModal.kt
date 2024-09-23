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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountModal(
    onDismiss: () -> Unit,
) {

//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true,
//        confirmValueChange = { false }  // This line prevents dismissal on swipe
//    )
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )

    var setup2fa by remember { mutableStateOf(false) }
    var is2faSettingsExpanded by remember { mutableStateOf(false) }
    var selected2faType by remember { mutableStateOf("TOTP") }
    var selectedHashFunction by remember { mutableStateOf("SHA1") }
    var period by remember { mutableStateOf("30") }
    var digits by remember { mutableStateOf("6") }

    ModalBottomSheet(
        shape = BottomSheetDefaults.HiddenShape,
        dragHandle = null,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    title = { Text("Add new account") },
                    navigationIcon = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AccountItem(icon = Icons.Outlined.Public, label = "Account")
                AccountItem(icon = Icons.Outlined.AccountCircle, label = "Username / Email")
                AccountItem(icon = Icons.Outlined.Password, label = "Password", isPassword = true)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                var urlList by remember { mutableStateOf(listOf("")) }

                urlList.forEachIndexed { index, url ->
                    UrlInputField(
                        url = url,
                        onUrlChange = { newUrl ->
                            urlList = urlList.toMutableList().also { it[index] = newUrl }
                        },
                        onRemoveUrl = {
                            urlList = urlList.toMutableList().also { it.removeAt(index) }
                        },
                        index = index,
                        urlListSize = urlList.size
                    )
                }
                Button(
                    onClick = {
                        urlList = urlList + ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Add another URL")
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = !setup2fa
                ) {
                    Button(
                        onClick = {
                            setup2fa = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Setup 2FA")
                    }
                }

                AnimatedVisibility(
                    visible = setup2fa
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {


                        AccountItem(icon = Icons.Outlined.Key, label = "Secret Key", is2fa = true)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Advanced 2FA settings
                        ListItem(
                            leadingContent = {
                                Icon(
                                    Icons.Outlined.Settings,
                                    contentDescription = "Advanced 2FA settings"
                                )
                            },
                            headlineContent = { Text("Advanced settings") },
                            trailingContent = {
                                IconButton(onClick = {
                                    is2faSettingsExpanded = !is2faSettingsExpanded
                                }) {
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
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .weight(1f),
                                        expanded = false,
                                        onExpandedChange = { }
                                    ) {
                                        OutlinedTextField(
                                            value = selected2faType,
                                            onValueChange = { },
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = false
                                                )
                                            },
                                            modifier = Modifier.menuAnchor()
                                        )
                                    }


                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .weight(1f),
                                        expanded = false,
                                        onExpandedChange = { }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedHashFunction,
                                            onValueChange = { },
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = false
                                                )
                                            },
                                            modifier = Modifier.menuAnchor()
                                        )
                                    }
                                }


                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .weight(1f),
                                        value = period,
                                        onValueChange = { period = it },
                                        label = { Text("Period (seconds)") }
                                    )



                                    OutlinedTextField(
                                        modifier = Modifier
                                            .weight(1f),
                                        value = digits,
                                        onValueChange = { digits = it },
                                        label = { Text("Digits") }
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))


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
    isPassword: Boolean = false,
    is2fa: Boolean = false
) {
    var password by remember { mutableStateOf("") }
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
            value = password,
            onValueChange = { password = it },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if ((isPassword || is2fa) && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}