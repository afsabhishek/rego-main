package com.rego.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Float.toWidthPx(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val baseWidth = 360f // current figma width
    val scale = screenWidth / baseWidth
    return (this * scale).dp
}

@Composable
fun Float.toHeightPx(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val baseHeight = 800f // current figma width
    val scale = screenHeight / baseHeight
    return (this * scale).dp
}

@Composable
fun Float.toPx(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val baseWidth = 360f // current figma width
    val scale = screenWidth / baseWidth
    return (this * scale).sp
}