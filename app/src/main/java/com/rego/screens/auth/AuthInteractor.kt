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

    suspend fun refreshTokenIfNeeded(): Flow<AuthResult> = flow {
        try {
            // First check if we have tokens
            val currentAuthToken = userPreferences.getAuthToken()
            val currentRefreshToken = userPreferences.getRefreshToken()
            val firebaseIdToken = userPreferences.getFirebaseIdToken()

            if (currentAuthToken == null || currentRefreshToken == null) {
                emit(AuthResult.NotAuthenticated)
                return@flow
            }

            // Check if backend token is expired
            val isBackendTokenExpired = userPreferences.isAuthTokenExpired()

            // Check if Firebase token is expired
            val isFirebaseTokenExpired = userPreferences.isFirebaseTokenExpired()

            if (!isBackendTokenExpired && !isFirebaseTokenExpired) {
                // Both tokens are valid
                emit(AuthResult.Authenticated(currentAuthToken, firebaseIdToken))
                return@flow
            }

            // Refresh Firebase token if needed
            if (isFirebaseTokenExpired) {
                firebaseAuthManager.refreshToken().first().let { result ->
                    when (result) {
                        is TokenResult.Success -> {
                            // Firebase token refreshed successfully
                            userPreferences.saveFirebaseIdToken(
                                result.token,
                                3600 // 1 hour expiry, adjust as needed
                            )
                        }
                        is TokenResult.Error -> {
                            // Failed to refresh Firebase token
                            emit(AuthResult.Error("Failed to refresh Firebase token: ${result.message}"))
                            return@flow
                        }
                    }
                }
            }

            // Refresh backend token if needed
            if (isBackendTokenExpired) {
                val response = authApi.refreshToken(currentRefreshToken)

                if (response.success && response.data != null) {
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
                    // Refresh failed, user needs to login again
                    userPreferences.clearAll()
                    firebaseAuthManager.signOut()
                    emit(AuthResult.RefreshFailed(response.message ?: "Session expired. Please login again."))
                }
            } else {
                // Backend token is valid, Firebase token was refreshed
                val updatedFirebaseToken = userPreferences.getFirebaseIdToken()
                emit(AuthResult.Authenticated(currentAuthToken, updatedFirebaseToken))
            }

        } catch (e: Exception) {
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