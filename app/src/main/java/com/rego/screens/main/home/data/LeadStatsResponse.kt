package com.rego.screens.main.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadStatsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: LeadStats? = null,
    @SerialName("message")
    val message: String? = null
) {
    @Serializable
    data class LeadStats(
        @SerialName("newLeads")
        val newLeads: Int,
        @SerialName("totalLeads")
        val totalLeads: Int,
        @SerialName("approved")
        val approved: Int,
        @SerialName("notRepairable")
        val notRepairable: Int,
        @SerialName("completed")
        val completed: Int,
        @SerialName("workInProgress")
        val workInProgress: Int
    )
}