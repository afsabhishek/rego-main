package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for /reference/register API
 * Contains insurance companies, states, and state-city mappings
 */
@Serializable
data class RegisterReferenceResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("data")
    val data: RegisterReferenceData? = null,
    
    @SerialName("message")
    val message: String? = null
) {
    @Serializable
    data class RegisterReferenceData(
        @SerialName("insuranceCompanies")
        val insuranceCompanies: List<InsuranceCompany>,
        
        @SerialName("states")
        val states: List<String>,
        
        @SerialName("stateCityMapping")
        val stateCityMapping: Map<String, List<String>>
    )
}