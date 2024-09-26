package com.ricsdev.uconnect.presentation.home.components.modal

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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.Key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.ricsdev.uconnect.Platform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoFaSetup(
    onDismiss: () -> Unit,
) {
    val viewModel = koinViewModel<TwoFaSetupViewModel>()
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
    onChangeCurrentTab: (Int) -> Unit,
) {

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