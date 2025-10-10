package com.rego.screens.auth

import com.rego.auth.FirebaseAuthManager
import com.rego.auth.TokenResult
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AuthInteractor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences,
    private val firebaseAuthManager: FirebaseAuthManager
) {

    fun refreshTokenIfNeeded(): Flow<AuthResult> = flow {
        try {
            // Get current tokens
            val currentAuthToken = userPreferences.getAuthToken()
            val currentRefreshToken = userPreferences.getRefreshToken()
            val firebaseIdToken = userPreferences.getFirebaseIdToken()

            println("🔍 Checking session...")
            println("Backend token exists: ${currentAuthToken != null}")
            println("Refresh token exists: ${currentRefreshToken != null}")
            println("Firebase token exists: ${firebaseIdToken != null}")

            // If no backend token or refresh token, user needs to login
            if (currentAuthToken == null || currentRefreshToken == null) {
                println("❌ No authentication tokens found")
                emit(AuthResult.NotAuthenticated)
                return@flow
            }

            // Check if backend token is expired
            val isBackendTokenExpired = userPreferences.isAuthTokenExpired()
            println("Backend token expired: $isBackendTokenExpired")

            // Check if Firebase token is expired (if exists)
            val isFirebaseTokenExpired = if (firebaseIdToken != null) {
                userPreferences.isFirebaseTokenExpired()
            } else {
                false
            }
            println("Firebase token expired: $isFirebaseTokenExpired")

            // If both tokens are valid, authenticate successfully
            if (!isBackendTokenExpired && !isFirebaseTokenExpired) {
                println("✅ All tokens valid - user authenticated")
                emit(AuthResult.Authenticated(currentAuthToken, firebaseIdToken))
                return@flow
            }

            // Refresh Firebase token if needed
            if (firebaseIdToken != null && isFirebaseTokenExpired) {
                println("🔄 Refreshing Firebase token...")
                try {
                    firebaseAuthManager.refreshToken().first().let { result ->
                        when (result) {
                            is TokenResult.Success -> {
                                println("✅ Firebase token refreshed")
                                userPreferences.saveFirebaseIdToken(result.token, 3600)
                            }
                            is TokenResult.Error -> {
                                println("⚠️ Firebase token refresh failed: ${result.message}")
                                // Continue with backend token only
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("⚠️ Firebase token refresh error: ${e.message}")
                    // Continue with backend token only
                }
            }

            // Refresh backend token if needed
            if (isBackendTokenExpired) {
                println("🔄 Refreshing backend token...")
                try {
                    val response = authApi.refreshToken(currentRefreshToken)

                    if (response.success && response.data != null) {
                        println("✅ Backend token refreshed")

                        // Save new backend tokens
                        userPreferences.saveAuthToken(
                            response.data.authToken,
                            response.data.expiresIn
                        )
                        userPreferences.saveRefreshToken(response.data.refreshToken)

                        // Get updated Firebase token
                        val updatedFirebaseToken = userPreferences.getFirebaseIdToken()

                        emit(AuthResult.TokenRefreshed(response.data.authToken, updatedFirebaseToken))
                    } else {
                        println("❌ Token refresh failed: ${response.message}")
                        // Refresh failed, clear data and require login
                        userPreferences.clearAll()
                        firebaseAuthManager.signOut()
                        emit(AuthResult.RefreshFailed(response.message ?: "Session expired. Please login again."))
                    }
                } catch (e: Exception) {
                    println("❌ Token refresh exception: ${e.message}")
                    e.printStackTrace()
                    userPreferences.clearAll()
                    firebaseAuthManager.signOut()
                    emit(AuthResult.RefreshFailed("Session expired. Please login again."))
                }
            } else {
                // Backend token is valid, Firebase token was refreshed if needed
                val updatedFirebaseToken = userPreferences.getFirebaseIdToken()
                emit(AuthResult.Authenticated(currentAuthToken, updatedFirebaseToken))
            }

        } catch (e: Exception) {
            println("❌ Authentication check failed: ${e.message}")
            e.printStackTrace()
            emit(AuthResult.Error(e.message ?: "Authentication check failed"))
        }
    }

    suspend fun authenticateWithFirebase(customToken: String): Flow<FirebaseAuthResult> = flow {
        try {
            // Sign in with custom token
            firebaseAuthManager.signInWithCustomToken(customToken).first().let { authResult ->
                when (authResult) {
                    is com.rego.auth.AuthResult.Success -> {
                        val user = authResult.user

                        // Get ID token
                        firebaseAuthManager.getIdToken(forceRefresh = false).first().let { tokenResult ->
                            when (tokenResult) {
                                is TokenResult.Success -> {
                                    // Save Firebase data
                                    userPreferences.saveFirebaseUid(user.uid)
                                    userPreferences.saveFirebaseCustomToken(customToken)
                                    userPreferences.saveFirebaseIdToken(
                                        tokenResult.token,
                                        3600 // 1 hour expiry
                                    )

                                    emit(FirebaseAuthResult.Success(user.uid, tokenResult.token))
                                }
                                is TokenResult.Error -> {
                                    emit(FirebaseAuthResult.Error("Failed to get ID token: ${tokenResult.message}"))
                                }
                            }
                        }
                    }
                    is com.rego.auth.AuthResult.Error -> {
                        emit(FirebaseAuthResult.Error(authResult.message))
                    }
                    is com.rego.auth.AuthResult.Loading -> {
                        // Continue waiting
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FirebaseAuthResult.Error("Firebase authentication failed: ${e.localizedMessage}"))
        }
    }

    suspend fun logout() {
        userPreferences.clearAll()
        firebaseAuthManager.signOut()
    }
}

// Auth result for general authentication flow
sealed class AuthResult {
    data class Authenticated(val backendToken: String, val firebaseToken: String?) : AuthResult()
    data class TokenRefreshed(val newBackendToken: String, val firebaseToken: String?) : AuthResult()
    object NotAuthenticated : AuthResult()
    data class RefreshFailed(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

// Firebase-specific auth result
sealed class FirebaseAuthResult {
    data class Success(val uid: String, val idToken: String) : FirebaseAuthResult()
    data class Error(val message: String) : FirebaseAuthResult()
}