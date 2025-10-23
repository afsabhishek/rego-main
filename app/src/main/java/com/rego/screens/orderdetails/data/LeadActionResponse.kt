package com.rego.screens.orderdetails.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadActionResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)