package com.rego


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CommonResponse<T>(
    @SerialName("data") var data: T?,
    @SerialName("status") var status: Boolean?,
    @SerialName("message") var message: String?,
)