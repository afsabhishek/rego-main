package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpResponse(
    @SerialName("success")
    val responseStatus: Boolean,
    @SerialName("data")
    val data: Data? = null
) {
    @Serializable
    data class Data(
        @SerialName("message")
        val message: String,
        @SerialName("otpSent")
        val otpSent: Boolean
    )
}