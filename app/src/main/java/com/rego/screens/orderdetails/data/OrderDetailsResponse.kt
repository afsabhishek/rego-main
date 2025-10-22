package com.rego.screens.orderdetails.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== Main Response ====================
@Serializable
data class OrderDetailsResponse(
    val success: Boolean,
    val data: OrderDetails?
) {

    // ==================== Order Details ====================
    @Serializable
    data class OrderDetails(
        @SerialName("_id")
        val id: String,
        val leadId: String,
        val status: String,
        val createdBy: String,
        val partType: String,
        val vehicle: VehicleDetails,
        val registrationNumber: String,
        val makeYear: Int,
        val inventoryPickUp: Boolean,
        val workshop: WorkshopDetails,
        val advisor: AdvisorDetails,
        val policyType: String,
        val claimNumber: String,
        val images: List<Image>,
        val activity: ActivityDetails,
        val createdAt: String,
        val updatedAt: String,
        @SerialName("__v")
        val version: Int,
        val inspectedBy: String
    )

    // ==================== Vehicle Details ====================
    @Serializable
    data class VehicleDetails(
        @SerialName("_id")
        val id: String,
        val make: String,
        val model: String,
        val variant: String,
        val fuelType: String,
        val image: String
    )

    // ==================== Workshop Details ====================
    @Serializable
    data class WorkshopDetails(
        @SerialName("_id")
        val id: String,
        val location: String,
        val dealerName: String,
        val address: String
    )

    // ==================== Advisor Details ====================
    @Serializable
    data class AdvisorDetails(
        val name: String,
        val contact: String
    )

    // ==================== Activity Details ====================
    @Serializable
    data class ActivityDetails(
        val lastUpdatedAt: String,
        val history: List<StatusHistory>
    )

    // ==================== Status History ====================
    @Serializable
    data class StatusHistory(
        val status: String,
        val timestamp: String,
        val notes: String,
        val updatedBy: String
    )

    @Serializable
    data class Image(
        val url: String,
        val thumbnailUrl: String,
        val originalName: String,
        val size: Int,
        val uploadedAt: String,
        val _id: String
    )
}