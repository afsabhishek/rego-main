package com.rego.screens.raiserequest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaiseRequestSubmitResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: RequestData? = null
) {
    @Serializable
    data class RequestData(
        @SerialName("message")
        val message: String,
        @SerialName("requestId")
        val requestId: String,
        @SerialName("requestStatus")
        val requestStatus: String,
        @SerialName("createdAt")
        val createdAt: String
    )
}