package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceCompany(
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("slug")
    val slug: String
)