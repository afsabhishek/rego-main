package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    @SerialName("authToken")
    val authToken: String,
    @SerialName("refreshToken")
    val refreshToken: String? = null,
    @SerialName("expiresIn")
    val expiresIn: Int,
    @SerialName("tokenType")
    val tokenType: String
)