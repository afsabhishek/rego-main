package com.rego.screens.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Request/Response models for refresh token
@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: RefreshTokenData? = null,
    @SerialName("message")
    val message: String? = null
) {
    @Serializable
    data class RefreshTokenData(
        @SerialName("authToken")
        val authToken: String,
        @SerialName("refreshToken")
        val refreshToken: String,
        @SerialName("expiresIn")
        val expiresIn: Int,
        @SerialName("tokenType")
        val tokenType: String
    )
}

// API Interface
interface AuthApi {
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse
    suspend fun validateToken(authToken: String): Boolean
}