package com.rego.screens.joinus

import com.rego.CommonResponse
import com.rego.screens.joinus.data.InsuranceCompaniesResponse
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.joinus.data.JoinUsResponse

interface JoinUsApi {
    suspend fun getInsuranceCompanies(): CommonResponse<InsuranceCompaniesResponse.InsuranceCompaniesData>

    suspend fun submitJoinUsRequest(
        request: JoinUsRequest
    ): CommonResponse<JoinUsResponse.JoinUsData>
}