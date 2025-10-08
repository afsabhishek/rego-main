package com.rego

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.rego.navigation.AppNavHost
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme

class MainActivity : ComponentActivity() {
    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            NativeAndroidBaseArchitectureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    AppNavHost()
                }
            }
        }
    }
}