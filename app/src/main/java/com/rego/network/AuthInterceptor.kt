package com.rego.network

import com.rego.screens.auth.AuthApi
import com.rego.util.UserPreferences
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class AuthInterceptor(
    private val userPreferences: UserPreferences,
    private val authApi: AuthApi
) {
    
    fun getAuthToken(): String? = runBlocking {
        userPreferences.getAuthToken()
    }
    
    fun refreshToken(): String? = runBlocking {
        try {
            val refreshToken = userPreferences.getRefreshToken()
            if (refreshToken != null) {
                val response = authApi.refreshToken(refreshToken)
                if (response.success && response.data != null) {
                    // Save new tokens
                    userPreferences.saveAuthToken(response.data.authToken)
                    userPreferences.saveRefreshToken(response.data.refreshToken)
                    return@runBlocking response.data.authToken
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@runBlocking null
    }
}

// Extension function to add auth header to requests
suspend fun HttpRequestBuilder.addAuthHeader(userPreferences: UserPreferences) {
    val token = userPreferences.getAuthToken()
    if (!token.isNullOrEmpty()) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}