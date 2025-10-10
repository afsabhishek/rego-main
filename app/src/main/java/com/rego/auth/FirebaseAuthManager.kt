package com.rego.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

sealed class TokenResult {
    data class Success(val token: String) : TokenResult()
    data class Error(val message: String) : TokenResult()
}

@Singleton
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth
) {

    /**
     * Get current user
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(): Boolean = auth.currentUser != null

    /**
     * Observe authentication state changes
     */
    fun getAuthStateFlow(): Flow<AuthResult> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                trySend(AuthResult.Success(user))
            } else {
                trySend(AuthResult.Error("User not authenticated"))
            }
        }

        auth.addAuthStateListener(authStateListener)

        // Send initial state
        val currentUser = auth.currentUser
        if (currentUser != null) {
            trySend(AuthResult.Success(currentUser))
        } else {
            trySend(AuthResult.Error("User not authenticated"))
        }

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * Sign in with email and password
     */
    fun signInWithEmailAndPassword(email: String, password: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                emit(AuthResult.Success(user))
            } else {
                emit(AuthResult.Error("Authentication failed: User is null"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error("Sign in failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Sign up with email and password
     */
    fun signUpWithEmailAndPassword(email: String, password: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                emit(AuthResult.Success(user))
            } else {
                emit(AuthResult.Error("Sign up failed: User is null"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error("Sign up failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Sign in with custom token (for backend authentication)
     */
    fun signInWithCustomToken(customToken: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val result = auth.signInWithCustomToken(customToken).await()
            val user = result.user

            if (user != null) {
                emit(AuthResult.Success(user))
            } else {
                emit(AuthResult.Error("Sign in with custom token failed: User is null"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error("Sign in with custom token failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Get ID token (refresh if needed)
     */
    fun getIdToken(forceRefresh: Boolean = false): Flow<TokenResult> = flow {
        val user = auth.currentUser

        if (user == null) {
            emit(TokenResult.Error("No authenticated user"))
            return@flow
        }

        try {
            val tokenResult = user.getIdToken(forceRefresh).await()
            val token = tokenResult.token

            if (token != null) {
                emit(TokenResult.Success(token))
            } else {
                emit(TokenResult.Error("Token is null"))
            }
        } catch (e: Exception) {
            emit(TokenResult.Error("Token refresh error: ${e.message ?: "Unknown error"}"))
        }
    }


    /**
     * Refresh ID token
     */
    fun refreshToken(): Flow<TokenResult> = getIdToken(forceRefresh = true)

    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            auth.sendPasswordResetEmail(email).await()
            emit(AuthResult.Success(auth.currentUser!!))
        } catch (e: Exception) {
            emit(AuthResult.Error("Password reset failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Update user profile
     */
    fun updateUserProfile(displayName: String? = null, photoUrl: String? = null): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val user = auth.currentUser
            if (user == null) {
                emit(AuthResult.Error("No authenticated user"))
                return@flow
            }

            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            displayName?.let { profileUpdates.setDisplayName(it) }
            photoUrl?.let { profileUpdates.setPhotoUri(android.net.Uri.parse(it)) }

            user.updateProfile(profileUpdates.build()).await()
            emit(AuthResult.Success(user))
        } catch (e: Exception) {
            emit(AuthResult.Error("Profile update failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Sign out
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Delete user account
     */
    fun deleteAccount(): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val user = auth.currentUser
            if (user == null) {
                emit(AuthResult.Error("No authenticated user"))
                return@flow
            }

            user.delete().await()
            emit(AuthResult.Success(user))
        } catch (e: Exception) {
            emit(AuthResult.Error("Account deletion failed: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Re-authenticate user (required before sensitive operations)
     */
    fun reAuthenticateWithEmailAndPassword(email: String, password: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)

            val user = auth.currentUser
            if (user == null) {
                emit(AuthResult.Error("No authenticated user"))
                return@flow
            }

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            emit(AuthResult.Success(user))
        } catch (e: Exception) {
            emit(AuthResult.Error("Re-authentication failed: ${e.message ?: "Unknown error"}"))
        }
    }
}