package com.rego.util

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.rego.ui.theme.Color00954D

@Composable
fun SetNavigationBarColor(
    color: Color = Color00954D,
    darkIcons: Boolean = isSystemInDarkTheme()
) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = color.toArgb()

        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightNavigationBars = darkIcons
    }
}