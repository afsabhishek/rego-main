package com.rego.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rego.screens.auth.AuthInteractor
import com.rego.screens.auth.AuthResult
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
            
            authInteractor.refreshTokenIfNeeded().collect { result ->
                when (result) {
                    is AuthResult.Authenticated -> {
                        // Token is still valid, go to home
                        _navigationEvent.value = SplashNavigationEvent.NavigateToHome
                    }
                    
                    is AuthResult.TokenRefreshed -> {
                        // Token was refreshed successfully, go to home
                        _navigationEvent.value = SplashNavigationEvent.NavigateToHome
                    }
                    
                    is AuthResult.NotAuthenticated -> {
                        // No tokens found, go to login
                        _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                    }
                    
                    is AuthResult.RefreshFailed -> {
                        // Refresh failed, need to login again
                        _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                    }
                    
                    is AuthResult.Error -> {
                        // Error occurred, go to login
                        _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                    }
                }
            }
            
            _isLoading.value = false
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