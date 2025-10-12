package com.rego.screens.raiserequest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*// Vehicle related models
@Serializable
data class VehicleMake(
    @SerialName("make")
    val make: String
)*/

@Serializable
data class VehicleModel(
    @SerialName("model")
    val model: String
)

@Serializable
data class VehicleVariant(
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

// Workshop related models
@Serializable
data class WorkshopLocation(
    @SerialName("location")
    val location: String
)

@Serializable
data class WorkshopDealer(
    @SerialName("_id")
    val id: String,
    @SerialName("dealerId")
    val dealerId: String? = null,
    @SerialName("dealerName")
    val dealerName: String,
    @SerialName("address")
    val address: String
) {
    // Helper to get the correct ID for submission
    val submissionId: String get() = dealerId ?: id
}

// Part type model
@Serializable
data class PartTypeReference(
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("icon")
    val icon: String? = null
)

// Create lead request model
@Serializable
data class CreateLeadRequest(
    @SerialName("partType")
    val partType: String, // slug from PartTypeReference
    @SerialName("vehicleId")
    val vehicleId: String,
    @SerialName("registrationNumber")
    val registrationNumber: String,
    @SerialName("makeYear")
    val makeYear: Int,
    @SerialName("inventoryPickUp")
    val inventoryPickUp: Boolean,
    @SerialName("workshopId")
    val workshopId: String,
    @SerialName("advisorName")
    val advisorName: String,
    @SerialName("advisorContact")
    val advisorContact: String,
    @SerialName("policyType")
    val policyType: String,
    @SerialName("claimNumber")
    val claimNumber: String
)

// Create lead response
@Serializable
data class CreateLeadResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: CreateLeadData? = null
) {
    @Serializable
    data class CreateLeadData(
        @SerialName("leadId")
        val leadId: String,
        @SerialName("_id")
        val id: String,
        @SerialName("status")
        val status: String,
        @SerialName("createdAt")
        val createdAt: String
    )
}

// Generic response wrappers
@Serializable
data class VehicleMakesResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<String>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class VehicleModelsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<String>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class VehicleVariantsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<VehicleVariant>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class WorkshopLocationsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<String>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class WorkshopDealersResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<WorkshopDealer>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class PartTypesResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: List<PartTypeReference>? = null,
    @SerialName("message")
    val message: String? = null
)

// Fuel type enum
enum class FuelType(val value: String, val displayName: String) {
    PETROL("petrol", "Petrol"),
    DIESEL("diesel", "Diesel"),
    CNG("cng", "CNG"),
    ELECTRIC("electric", "Electric"),
    HYBRID("hybrid", "Hybrid");

    companion object {
        fun fromValue(value: String): FuelType? = values().find { it.value == value }
        fun getAllDisplayNames(): List<String> = values().map { it.displayName }
    }
}

// Policy type enum
enum class PolicyType(val value: String, val displayName: String) {
    COMPREHENSIVE("comprehensive", "Comprehensive"),
    THIRD_PARTY("third_party", "Third Party"),
    ZERO_DEPRECIATION("zero_depreciation", "Zero Depreciation"),
    RETURN_TO_INVOICE("return_to_invoice", "Return to Invoice");

    companion object {
        fun fromDisplayName(name: String): PolicyType? = values().find { it.displayName == name }
        fun getAllDisplayNames(): List<String> = values().map { it.displayName }
    }
}