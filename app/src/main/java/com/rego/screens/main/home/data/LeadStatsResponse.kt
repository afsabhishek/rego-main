package com.rego.screens.main.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadStatsResponse(

    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<LeadStatItem>?
) {
    @Serializable
    data class LeadStatItem(
        @SerialName("label")
        val label: String,
        @SerialName("count")
        val count: Int,
        @SerialName("status")
        val status: List<String>
    )
}