package com.rego.network

import com.rego.auth.FirebaseAuthManager
import com.rego.auth.TokenResult
import com.rego.screens.auth.AuthApi
import com.rego.util.UserPreferences
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first

class AuthInterceptor(
    private val userPreferences: UserPreferences,
    private val authApi: AuthApi,
    private val firebaseAuthManager: FirebaseAuthManager
) {

    suspend fun getAuthToken(): String? {
        return userPreferences.getAuthToken()
    }

    suspend fun getFirebaseIdToken(forceRefresh: Boolean = false): String? {
        return try {
            // Check if Firebase token is expired or force refresh is requested
            if (userPreferences.isFirebaseTokenExpired() || forceRefresh) {
                // Refresh Firebase token
                val result = firebaseAuthManager.getIdToken(forceRefresh = true).first()

                when (result) {
                    is TokenResult.Success -> {
                        // Save the new token
                        userPreferences.saveFirebaseIdToken(
                            result.token,
                            3600 // 1 hour expiry, adjust as needed
                        )
                        result.token
                    }
                    is TokenResult.Error -> {
                        // Token refresh failed, return cached token if available
                        userPreferences.getFirebaseIdToken()
                    }
                }
            } else {
                // Return cached token
                userPreferences.getFirebaseIdToken()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Return cached token as fallback
            userPreferences.getFirebaseIdToken()
        }
    }

    suspend fun refreshBackendToken(): String? {
        return try {
            val refreshToken = userPreferences.getRefreshToken()
            if (refreshToken != null) {
                val response = authApi.refreshToken(refreshToken)
                if (response.success && response.data != null) {
                    // Save new tokens
                    userPreferences.saveAuthToken(
                        response.data.authToken,
                        response.data.expiresIn
                    )
                    userPreferences.saveRefreshToken(response.data.refreshToken)
                    return response.data.authToken
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun refreshBothTokens(): Pair<String?, String?> {
        val backendToken = refreshBackendToken()
        val firebaseToken = getFirebaseIdToken(forceRefresh = true)
        return Pair(backendToken, firebaseToken)
    }
}

// Extension function to add backend auth header
suspend fun HttpRequestBuilder.addBackendAuthHeader(userPreferences: UserPreferences) {
    val token = userPreferences.getAuthToken()
    if (!token.isNullOrEmpty()) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}

// Extension function to add Firebase auth header
suspend fun HttpRequestBuilder.addFirebaseAuthHeader(userPreferences: UserPreferences) {
    val firebaseToken = userPreferences.getFirebaseIdToken()
    if (!firebaseToken.isNullOrEmpty()) {
        header("X-Firebase-Token", firebaseToken)
    }
}

// Extension function to add both headers
suspend fun HttpRequestBuilder.addBothAuthHeaders(userPreferences: UserPreferences) {
    addBackendAuthHeader(userPreferences)
    addFirebaseAuthHeader(userPreferences)
}

// Extension function to add backend auth header with interceptor
suspend fun HttpRequestBuilder.addBackendAuthHeader(interceptor: AuthInterceptor) {
    val token = interceptor.getAuthToken()
    if (!token.isNullOrEmpty()) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}

// Extension function to add Firebase auth header with interceptor
suspend fun HttpRequestBuilder.addFirebaseAuthHeader(interceptor: AuthInterceptor) {
    val firebaseToken = interceptor.getFirebaseIdToken(forceRefresh = false)
    if (!firebaseToken.isNullOrEmpty()) {
        header("X-Firebase-Token", firebaseToken)
    }
}

// Extension function to add both headers with interceptor
suspend fun HttpRequestBuilder.addBothAuthHeaders(interceptor: AuthInterceptor) {
    addBackendAuthHeader(interceptor)
    addFirebaseAuthHeader(interceptor)
}