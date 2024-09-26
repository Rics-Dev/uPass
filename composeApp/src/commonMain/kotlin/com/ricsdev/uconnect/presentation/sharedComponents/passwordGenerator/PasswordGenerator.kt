package com.ricsdev.uconnect.presentation.sharedComponents.passwordGenerator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGenerator(
    isAddingAccount: Boolean = false,
    usePassword: (String) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val viewModel = koinViewModel<PasswordGeneratorViewModel>()
    val passwordState by viewModel.passwordState.collectAsStateWithLifecycle()

    var showAdvancedOptions by remember { mutableStateOf(false) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { },
                label = { Text("Generated Password") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { viewModel.generatePassword() }) {
                    Icon(Icons.Rounded.LockReset, contentDescription = "Generate Password")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Generate")
                }
                if(isAddingAccount){
                    Button(onClick = { usePassword(passwordState.value) }) {
                        Icon(Icons.Rounded.Password, contentDescription = "Use Password")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use")
                    }
                }
                Button(onClick = {
                    clipboardManager.setText(AnnotatedString(passwordState.value))
                }) {
                    Icon(Icons.Rounded.ContentPaste, contentDescription = "Copy Password")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { showAdvancedOptions = !showAdvancedOptions }) {
                Text(if (showAdvancedOptions) "Hide Advanced Options" else "Show Advanced Options")
            }

            AnimatedVisibility(visible = showAdvancedOptions) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Length: ${passwordState.length}")
                    Slider(
                        value = passwordState.length.toFloat(),
                        onValueChange = { viewModel.updatePasswordState { copy(length = it.toInt()) } },
                        onValueChangeFinished = { viewModel.generatePassword() },
                        valueRange = 5f..128f,
                        steps = 122
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include Uppercase (A-Z)")
                        Switch(
                            checked = passwordState.useUppercase,
                            onCheckedChange = {
                                viewModel.updatePasswordState { copy(useUppercase = it) }
                                viewModel.generatePassword()
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include Lowercase (a-z)")
                        Switch(
                            checked = passwordState.useLowercase,
                            onCheckedChange = {
                                viewModel.updatePasswordState { copy(useLowercase = it) }
                                viewModel.generatePassword()
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include Numbers (0-9)")
                        Switch(
                            checked = passwordState.useNumbers,
                            onCheckedChange = {
                                viewModel.updatePasswordState { copy(useNumbers = it) }
                                viewModel.generatePassword()
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include Symbols (!@#$%^&*)")
                        Switch(
                            checked = passwordState.useSymbols,
                            onCheckedChange = {
                                viewModel.updatePasswordState { copy(useSymbols = it) }
                                viewModel.generatePassword()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsRange(
                        title = "Minimum Numbers",
                        value = passwordState.minNumbers,
                        onValueChange = {
                            viewModel.updatePasswordState { copy(minNumbers = it) }
                            viewModel.generatePassword()
                        },
                        valueRange = 1..passwordState.length,
                        valueDisplay = { passwordState.minNumbers.toString() },
                    )

                    SettingsRange(
                        title = "Minimum Symbols",
                        value = passwordState.minSymbols,
                        onValueChange = {
                            viewModel.updatePasswordState { copy(minSymbols = it) }
                            viewModel.generatePassword()
                        },
                        valueRange = 1..passwordState.length,
                        valueDisplay = { passwordState.minSymbols.toString() },
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Avoid Ambiguous Characters (Il1O0)")
                        Switch(
                            checked = passwordState.avoidAmbiguous,
                            onCheckedChange = {
                                viewModel.updatePasswordState { copy(avoidAmbiguous = it) }
                                viewModel.generatePassword()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRange(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    valueDisplay: (Int) -> String,
) {
    Text(title)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val newSize = (value - 1).coerceIn(valueRange.first, valueRange.last)
                onValueChange(newSize)
            }
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease $title")
        }
        Text(valueDisplay(value), style = MaterialTheme.typography.bodyLarge)
        IconButton(
            onClick = {
                val newSize = (value + 1).coerceIn(valueRange.first, valueRange.last)
                onValueChange(newSize)
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase $title")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}