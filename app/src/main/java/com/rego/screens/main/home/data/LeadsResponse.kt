package com.rego.screens.main.home.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: LeadsData? = null,
    @SerialName("message")
    val message: String? = null
) {
    @Serializable
    data class LeadsData(
        @SerialName("leads")
        val leads: List<Lead>,
        @SerialName("pagination")
        val pagination: Pagination,
        @SerialName("filters")
        val filters: Filters? = null
    ) {
        @Serializable
        data class Lead(
            @SerialName("_id")
            val id: String,
            @SerialName("leadId")
            val leadId: String,
            @SerialName("status")
            val status: String,
            @SerialName("partType")
            val partType: String,
            @SerialName("vehicle")
            val vehicle: Vehicle,
            @SerialName("registrationNumber")
            val registrationNumber: String,
            @SerialName("makeYear")
            val makeYear: Int,
            @SerialName("inventoryPickUp")
            val inventoryPickUp: Boolean,
            @SerialName("dealer")
            val dealer: Dealer? = null,
            @SerialName("advisor")
            val advisor: Advisor,
            @SerialName("policyType")
            val policyType: String,
            @SerialName("claimNumber")
            val claimNumber: String,
            @SerialName("activity")
            val activity: Activity,
            @SerialName("createdAt")
            val createdAt: String
        )

        @Serializable
        data class Vehicle(
            @SerialName("_id")
            val id: String,
            @SerialName("make")
            val make: String,
            @SerialName("model")
            val model: String,
            @SerialName("variant")
            val variant: String,
            @SerialName("fuelType")
            val fuelType: String,
            @SerialName("image")
            val image: String? = null
        )

        @Serializable
        data class Dealer(
            @SerialName("location")
            val location: String,
            @SerialName("name")
            val name: String
        )

        @Serializable
        data class Advisor(
            @SerialName("name")
            val name: String,
            @SerialName("contact")
            val contact: String
        )

        @Serializable
        data class Activity(
            @SerialName("lastUpdatedAt")
            val lastUpdatedAt: String
        )

        @Serializable
        data class Pagination(
            @SerialName("currentPage")
            val currentPage: Int = 1,

            @SerialName("totalPages")
            val totalPages: Int = 1,

            @SerialName("totalCount")
            val totalCount: Int = 0,

            @SerialName("limit")
            val limit: Int = 20,

            @SerialName("offset")
            val offset: Int = 0,

            @SerialName("hasNextPage")
            val hasNextPage: Boolean = false,

            @SerialName("hasPrevPage")
            val hasPrevPage: Boolean = false
        ) {
            // Backward compatibility properties
            val page: Int get() = currentPage
            val total: Int get() = totalCount
            val pages: Int get() = totalPages
            val hasNext: Boolean get() = hasNextPage
            val hasPrev: Boolean get() = hasPrevPage
        }

        @Serializable
        data class Filters(
            @SerialName("status")
            val status: String? = null,
            @SerialName("partType")
            val partType: String? = null,
            @SerialName("registrationNumber")
            val registrationNumber: String? = null,
            @SerialName("claimNumber")
            val claimNumber: String? = null
        )
    }
}

enum class LeadStatus(val value: String, val displayName: String) {
    NEW("NEW", "New Leads"),
    APPROVED("APPROVED", "Approved"),
    NOT_REPAIRABLE("NOT_REPAIRABLE", "Not Repairable"),
    COMPLETED("COMPLETED", "Completed"),
    WORK_IN_PROGRESS("WORK_IN_PROGRESS", "Work In Progress"),
    PICKUP_ALIGNED("PICKUP_ALIGNED", "Pickup Aligned"),
    PART_DELIVERED("PART_DELIVERED", "Part Delivered"),
    PICKUP_DONE("PICKUP_DONE", "Pickup Done"),
    INVOICE_GENERATED("INVOICE_GENERATED", "Invoice Generated"),
    READY_FOR_DELIVERY("READY_FOR_DELIVERY", "Ready for Delivery");

    companion object {
        fun fromValue(value: String): LeadStatus? = values().find { it.value == value }
    }
}