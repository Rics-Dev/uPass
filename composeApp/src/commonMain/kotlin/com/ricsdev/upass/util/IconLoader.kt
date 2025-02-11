package com.ricsdev.upass.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import upass.composeapp.generated.resources.Res
import upass.composeapp.generated.resources.allDrawableResources


@OptIn(ExperimentalResourceApi::class)
@Composable
fun loadIcon(account: String): Painter? {
    val domain = account.substringAfter(".").substringBefore(".")
    val drawableResource =
        Res.allDrawableResources.entries.find { it.key.contains(domain, ignoreCase = true) }?.value
    return if (drawableResource != null) {
        painterResource(drawableResource)
    } else {
        null
    }
}
