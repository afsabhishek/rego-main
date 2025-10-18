package com.rego.screens.orderdetails

import com.rego.screens.main.home.data.LeadsResponse

interface OrderDetailsApi {
    suspend fun getLeadById(authToken: String, leadId: String): LeadsResponse
    suspend fun getLeadsByStatus(
        authToken: String,
        status: String?,
        partType: String?,
    ): LeadsResponse
}