package com.rego.screens.mobileverification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("_id")
    val id: String,
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
    val role: String,
    @SerialName("firebaseUid")
    val firebaseUid: String,
    @SerialName("isPhoneVerified")
    val isPhoneVerified: Boolean,
    @SerialName("isManuallyActivated")
    val isManuallyActivated: Boolean,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String
)
