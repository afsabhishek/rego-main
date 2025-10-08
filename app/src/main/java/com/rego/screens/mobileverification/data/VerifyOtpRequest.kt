package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequest(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("otp")
    val otp: String,
)