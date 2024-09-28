package com.ricsdev.uconnect.util

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun PlatformVerticalScrollbar(modifier: Modifier, scrollState: LazyListState) {

    val customScrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
        unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f),
        hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
    )


    VerticalScrollbar(
        style = customScrollbarStyle,
        modifier = modifier,
        adapter = rememberScrollbarAdapter(scrollState = scrollState)
    )
}