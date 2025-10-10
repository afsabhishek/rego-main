package com.rego.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        // Backend tokens
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        // Firebase tokens
        private val FIREBASE_ID_TOKEN_KEY = stringPreferencesKey("firebase_id_token")
        private val FIREBASE_CUSTOM_TOKEN_KEY = stringPreferencesKey("firebase_custom_token")
        private val FIREBASE_UID_KEY = stringPreferencesKey("firebase_uid")

        // Token expiry
        private val TOKEN_EXPIRY_TIME_KEY = longPreferencesKey("token_expiry_time")
        private val FIREBASE_TOKEN_EXPIRY_TIME_KEY = longPreferencesKey("firebase_token_expiry_time")

        // User info
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
    }

    // ==================== Backend Token Management ====================

    suspend fun saveAuthToken(token: String, expiresIn: Int = 3600) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000L)
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
            preferences[TOKEN_EXPIRY_TIME_KEY] = expiryTime
        }
    }

    suspend fun getAuthToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[AUTH_TOKEN_KEY] }
            .first()
    }

    suspend fun isAuthTokenExpired(): Boolean {
        val expiryTime = context.dataStore.data
            .map { preferences -> preferences[TOKEN_EXPIRY_TIME_KEY] ?: 0L }
            .first()

        return System.currentTimeMillis() > expiryTime
    }

    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
            .first()
    }

    // ==================== Firebase Token Management ====================

    suspend fun saveFirebaseIdToken(token: String, expiresIn: Int = 3600) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000L)
        context.dataStore.edit { preferences ->
            preferences[FIREBASE_ID_TOKEN_KEY] = token
            preferences[FIREBASE_TOKEN_EXPIRY_TIME_KEY] = expiryTime
        }
    }

    suspend fun getFirebaseIdToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[FIREBASE_ID_TOKEN_KEY] }
            .first()
    }

    suspend fun isFirebaseTokenExpired(): Boolean {
        val expiryTime = context.dataStore.data
            .map { preferences -> preferences[FIREBASE_TOKEN_EXPIRY_TIME_KEY] ?: 0L }
            .first()

        // Check if token expires in less than 5 minutes
        return System.currentTimeMillis() > (expiryTime - 300000L)
    }

    suspend fun saveFirebaseCustomToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[FIREBASE_CUSTOM_TOKEN_KEY] = token
        }
    }

    suspend fun getFirebaseCustomToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[FIREBASE_CUSTOM_TOKEN_KEY] }
            .first()
    }

    suspend fun saveFirebaseUid(uid: String) {
        context.dataStore.edit { preferences ->
            preferences[FIREBASE_UID_KEY] = uid
        }
    }

    suspend fun getFirebaseUid(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[FIREBASE_UID_KEY] }
            .first()
    }

    // ==================== User Info Management ====================

    suspend fun saveUserInfo(
        userId: String,
        userName: String,
        email: String? = null,
        phone: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            email?.let { preferences[USER_EMAIL_KEY] = it }
            phone?.let { preferences[USER_PHONE_KEY] = it }
        }
    }

    suspend fun getUserId(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[USER_ID_KEY] }
            .first()
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[USER_NAME_KEY] }
            .first()
    }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] }

    // ==================== Clear Data ====================

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearFirebaseData() {
        context.dataStore.edit { preferences ->
            preferences.remove(FIREBASE_ID_TOKEN_KEY)
            preferences.remove(FIREBASE_CUSTOM_TOKEN_KEY)
            preferences.remove(FIREBASE_UID_KEY)
            preferences.remove(FIREBASE_TOKEN_EXPIRY_TIME_KEY)
        }
    }

    suspend fun clearBackendTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(TOKEN_EXPIRY_TIME_KEY)
        }
    }
}