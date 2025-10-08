package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceCompaniesResponse(
    @SerialName("success")
    val responseStatus: Boolean,
    @SerialName("data")
    val data: InsuranceCompaniesData? = null
) {
    @Serializable
    data class InsuranceCompaniesData(
        @SerialName("insuranceCompanies")
        val insuranceCompanies: List<InsuranceCompany>,
        @SerialName("pagination")
        val pagination: PaginationInfo
    )
}