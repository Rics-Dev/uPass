package com.ricsdev.uconnect.presentation.home.components.modal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ricsdev.uconnect.PlatformType
import com.ricsdev.uconnect.getPlatform
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ricsdev.uconnect.Platform
import com.ricsdev.uconnect.domain.model.HmacAlgorithm
import com.ricsdev.uconnect.domain.model.OtpDigits
import com.ricsdev.uconnect.domain.model.OtpPeriod
import com.ricsdev.uconnect.domain.model.OtpType
import com.ricsdev.uconnect.domain.model.TwoFaSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFaSetup(
    onDismiss: () -> Unit,
) {
    val viewModel = koinViewModel<TwoFaSetupViewModel>()
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val platform = getPlatform()

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = when (selectedTab) {
                    0 -> Icons.Outlined.Key
                    1 -> Icons.Default.QrCodeScanner
                    2 -> Icons.Outlined.Edit
                    3 -> Icons.Default.ImageSearch
                    else -> {
                        Icons.Outlined.Key
                    }
                },
                contentDescription = "Setup 2FA",
                modifier = Modifier.size(48.dp)
            )
            when (selectedTab) {
                0 -> Setup2fa(
                    platform,
                    onChangeCurrentTab = { selectedTab = it }
                )

                1 -> Setup2fa(
                    platform,
                    onChangeCurrentTab = { selectedTab = it }
                )

                2 -> SetupManually(
                    viewModel = viewModel,
                    twoFaSettings = accountState.twoFaSettings!!,
                    onTwoFaSettingsChange = {  },
                    onChangeCurrentTab = { selectedTab = it }
                )

                3 -> Setup2fa(
                    platform,
                    onChangeCurrentTab = { selectedTab = it }
                )
            }
        }
    }
}


@Composable
fun SetupManually(
    viewModel: TwoFaSetupViewModel,
    twoFaSettings: TwoFaSettings,
    onTwoFaSettingsChange: (TwoFaSettings) -> Unit,
    onChangeCurrentTab: (Int) -> Unit,
) {

    val accountState by viewModel.accountState.collectAsStateWithLifecycle()
    var is2faSettingsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Manual 2FA Setup",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = accountState.name,
                onValueChange = { viewModel.updateAccountState(accountState.copy(name = it)) },
                label = { Text("Account") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = accountState.username,
                onValueChange = { viewModel.updateAccountState(accountState.copy(username = it)) },
                label = { Text("Username") },
                modifier = Modifier.weight(1f)
            )
        }




        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = accountState.twoFaSettings!!.secret,
            onValueChange = { newSecret ->
                accountState.twoFaSettings?.let { settings ->
                    viewModel.updateAccountState(accountState.copy(twoFaSettings = settings.copy(secret = newSecret)))
                }
            },
            label = { Text("Secret Key") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        ) {
            ListItem(
                modifier = Modifier.clickable(
                    onClick = { is2faSettingsExpanded = !is2faSettingsExpanded }
                ),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
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
                                onTwoFaSettingsChange(twoFaSettings.copy(hmacAlgorithm = it))
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
                    if (twoFaSettings.type == OtpType.HOTP) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = twoFaSettings.counter.toString(),
                            onValueChange = {
                                val newCounter = it.toLongOrNull() ?: 0
                                onTwoFaSettingsChange(twoFaSettings.copy(counter = newCounter))
                            },
                            label = { Text("Counter") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveAccount()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save 2FA Service")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpTypeDropdown(
    selectedType: OtpType,
    onTypeSelected: (OtpType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("OTP Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            OtpType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
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
fun HmacAlgorithmDropdown(
    selectedAlgorithm: HmacAlgorithm,
    onAlgorithmSelected: (HmacAlgorithm) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedAlgorithm.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("HMAC Algorithm") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            HmacAlgorithm.entries.forEach { algorithm ->
                DropdownMenuItem(
                    text = { Text(algorithm.name) },
                    onClick = {
                        onAlgorithmSelected(algorithm)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotpPeriodDropdown(
    selectedPeriod: OtpPeriod,
    onPeriodSelected: (OtpPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = "${selectedPeriod.seconds} seconds",
            onValueChange = {},
            readOnly = true,
            label = { Text("TOTP Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            OtpPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text("${period.seconds} seconds") },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpDigitsDropdown(
    selectedDigits: OtpDigits,
    onDigitsSelected: (OtpDigits) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedDigits.number.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("OTP Digits") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            OtpDigits.entries.forEach { digits ->
                DropdownMenuItem(
                    text = { Text(digits.number.toString()) },
                    onClick = {
                        onDigitsSelected(digits)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun Setup2fa(
    platform: Platform,
    onChangeCurrentTab: (Int) -> Unit,
) {
    Text(
        text = "Setup 2FA",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    if (platform.type != PlatformType.DESKTOP) {
        ListItem(
            leadingContent = {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR code")
            },
            headlineContent = { Text("Scan QR code") },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onChangeCurrentTab(1) }
        )
    }
    ListItem(
        leadingContent = {
            Icon(Icons.Outlined.Edit, contentDescription = "Enter manually")
        },
        headlineContent = { Text("Enter manually") },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onChangeCurrentTab(2) }
    )
    ListItem(
        leadingContent = {
            Icon(Icons.Default.ImageSearch, contentDescription = "Scan image")
        },
        headlineContent = { Text("Scan image") },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onChangeCurrentTab(3) }
    )
}