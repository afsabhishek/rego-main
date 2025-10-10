package com.rego.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.ColorFFFFFF
import com.rego.ui.theme.ColorFFFFFF_28
import com.rego.ui.theme.fontBoldMontserrat
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    gotoLoginOptionScreen: () -> Unit,
    gotoHomeScreen: () -> Unit
) {
    val viewModel: SplashViewModel = koinViewModel()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Check session when screen loads
    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SplashNavigationEvent.NavigateToHome -> {
                gotoHomeScreen()
                viewModel.clearNavigationEvent()
            }
            is SplashNavigationEvent.NavigateToLogin -> {
                gotoLoginOptionScreen()
                viewModel.clearNavigationEvent()
            }
            null -> {
                // No navigation event yet
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color00954D),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.rego_brandmark_1),
                contentDescription = "Rego Logo",
            )
            val gradientColors = listOf(ColorFFFFFF_28(), ColorFFFFFF.copy(0.7f), ColorFFFFFF_28())
            Box(
                modifier = Modifier
                    .alpha(0.3f)
                    .width(170.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
            )
            Spacer(modifier = Modifier.size(21.dp))
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = "INSURER APP",
                style = fontBoldMontserrat().copy(color = Color.White, letterSpacing = 4.sp)
            )
        }
    }
}