package com.rego.screens.main.profile.data

import com.rego.screens.mobileverification.data.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: ProfileResponseData? = null,
    @SerialName("message")
    val message: String? = null
) {
    @Serializable
    data class ProfileResponseData(
        @SerialName("user")
        val user: User
    )
}