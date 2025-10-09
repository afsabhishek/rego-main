package com.rego.screens.auth

import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthInteractor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences
) {
    
    suspend fun refreshTokenIfNeeded(): Flow<AuthResult> = flow {
        try {
            // First check if we have tokens
            val currentAuthToken = userPreferences.getAuthToken()
            val currentRefreshToken = userPreferences.getRefreshToken()
            
            if (currentAuthToken == null || currentRefreshToken == null) {
                emit(AuthResult.NotAuthenticated)
                return@flow
            }
            
            // Try to validate current auth token
            val isValid = authApi.validateToken(currentAuthToken)
            
            if (isValid) {
                emit(AuthResult.Authenticated(currentAuthToken))
                return@flow
            }
            
            // Token is invalid or expired, try to refresh
            val response = authApi.refreshToken(currentRefreshToken)
            
            if (response.success && response.data != null) {
                // Save new tokens
                userPreferences.saveAuthToken(response.data.authToken)
                userPreferences.saveRefreshToken(response.data.refreshToken)
                
                emit(AuthResult.TokenRefreshed(response.data.authToken))
            } else {
                // Refresh failed, user needs to login again
                userPreferences.clearAll()
                emit(AuthResult.RefreshFailed(response.message ?: "Session expired. Please login again."))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AuthResult.Error(e.message ?: "Authentication check failed"))
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAll()
    }
}

sealed class AuthResult {
    data class Authenticated(val token: String) : AuthResult()
    data class TokenRefreshed(val newToken: String) : AuthResult()
    object NotAuthenticated : AuthResult()
    data class RefreshFailed(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}