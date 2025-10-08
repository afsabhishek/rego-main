package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("phoneNumber")
    val phoneNumber: String,
)