package com.ricsdev.upass.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun HomeFloatingActionButton(
    addAccount: () -> Unit,
    showPasswordGenerator: () -> Unit,
    show2faSetup: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically { it } + expandVertically() + fadeIn(),
            exit = slideOutVertically { it } + shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    FilledTonalButton(
                        contentPadding = PaddingValues(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { showPasswordGenerator() }
                    ) {
                        Text("Password Generator")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(
                        onClick = { showPasswordGenerator() },
                    ) {
                        Icon(Icons.Rounded.Password, contentDescription = "Search")
                    }
                }
                Row {
                    FilledTonalButton(
                        contentPadding = PaddingValues(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { addAccount() }
                    ) {
                        Text("Account")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(
                        onClick = { addAccount() },
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = "add account")
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    FilledTonalButton(
                        contentPadding = PaddingValues(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        onClick = { show2faSetup() }
                    ) {
                        Text("2FA")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallFloatingActionButton(
                        onClick = { show2faSetup() },
                    ) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Search")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        val rotationAngle by animateFloatAsState(if (isExpanded) 135f else 0f)

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = if (isExpanded) "Close" else "Add",
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotationAngle
                }
            )
        }
    }
}