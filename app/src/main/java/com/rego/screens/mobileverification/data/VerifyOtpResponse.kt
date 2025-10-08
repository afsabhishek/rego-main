package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: LoginData
) {
    @Serializable
    data class LoginData(
        @SerialName("message")
        val message: String,
        @SerialName("user")
        val user: User,
        @SerialName("authentication")
        val authentication: Authentication
    )
}