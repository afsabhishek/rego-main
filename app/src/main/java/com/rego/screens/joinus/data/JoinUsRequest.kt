package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinUsRequest(
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("city")
    val city: String,
    @SerialName("state")
    val state: String,
    @SerialName("insuranceCompany")
    val insuranceCompany: String,
    @SerialName("role")
    val role: String
)