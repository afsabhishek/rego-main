package com.rego.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rego.screens.auth.AuthInteractor
import com.rego.screens.auth.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val _navigationEvent = MutableStateFlow<SplashNavigationEvent?>(null)
    val navigationEvent: StateFlow<SplashNavigationEvent?> = _navigationEvent.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            _isLoading.value = true

            // Add minimum splash display time for better UX
            delay(1000)

            try {
                authInteractor.refreshTokenIfNeeded().collect { result ->
                    when (result) {
                        is AuthResult.Authenticated -> {
                            // Valid session exists, go directly to home
                            println("✅ Session valid - navigating to home")
                            _navigationEvent.value = SplashNavigationEvent.NavigateToHome
                            _isLoading.value = false
                        }

                        is AuthResult.TokenRefreshed -> {
                            // Token was successfully refreshed, go to home
                            println("✅ Token refreshed - navigating to home")
                            _navigationEvent.value = SplashNavigationEvent.NavigateToHome
                            _isLoading.value = false
                        }

                        is AuthResult.NotAuthenticated -> {
                            // No session found, go to login
                            println("❌ No session - navigating to login")
                            _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                            _isLoading.value = false
                        }

                        is AuthResult.RefreshFailed -> {
                            // Session expired or refresh failed, go to login
                            println("❌ Session expired: ${result.message}")
                            _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                            _isLoading.value = false
                        }

                        is AuthResult.Error -> {
                            // Error occurred, go to login
                            println("❌ Auth error: ${result.message}")
                            _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                            _isLoading.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ Exception during session check: ${e.message}")
                e.printStackTrace()
                _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                _isLoading.value = false
            }
        }
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
}

sealed class SplashNavigationEvent {
    object NavigateToHome : SplashNavigationEvent()
    object NavigateToLogin : SplashNavigationEvent()
}