package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinUsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: JoinUsData? = null
) {
    @Serializable
    data class JoinUsData(
        @SerialName("message")
        val message: String,
        @SerialName("userId")
        val userId: String,
        @SerialName("firebaseUid")
        val firebaseUid: String
    )
}