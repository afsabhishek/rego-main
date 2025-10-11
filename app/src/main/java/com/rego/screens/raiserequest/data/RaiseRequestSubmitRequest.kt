package com.rego.screens.raiserequest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaiseRequestSubmitRequest(
    @SerialName("authToken")
    val authToken: String = "", // Will be injected from stored session
    @SerialName("userId")
    val userId: String,
    @SerialName("userName")
    val userName: String,
    @SerialName("userPhone")
    val userPhone: String,
    @SerialName("userEmail")
    val userEmail: String,
    @SerialName("insuranceCompany")
    val insuranceCompany: String,
    @SerialName("selectedCarMake")
    val selectedCarMake: String,
    @SerialName("selectedCarModel")
    val selectedCarModel: String,
    @SerialName("selectedFuelType")
    val selectedFuelType: String,
    @SerialName("selectedCarVariant")
    val selectedCarVariant: String,
    @SerialName("selectedDealerLocation")
    val selectedDealerLocation: String,
    @SerialName("selectedPolicyType")
    val selectedPolicyType: String,
    @SerialName("dealerName")
    val dealerName: String,
    @SerialName("advisorName")
    val advisorName: String,
    @SerialName("advisorContactNumber")
    val advisorContactNumber: String,
    @SerialName("claimNumber")
    val claimNumber: String,
    @SerialName("carRegNumber")
    val carRegNumber: String,
    @SerialName("makeYear")
    val makeYear: String,
    @SerialName("isInventoryPickup")
    val isInventoryPickup: Boolean,
    @SerialName("selectedPartType")
    val selectedPartType: String,
    @SerialName("images")
    val images: List<String>
)