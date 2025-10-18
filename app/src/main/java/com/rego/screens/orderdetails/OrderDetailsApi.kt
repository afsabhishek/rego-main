package com.rego.screens.orderdetails

import com.rego.screens.main.home.data.LeadsResponse

interface OrderDetailsApi {
    suspend fun getLeadById(authToken: String, leadId: String): LeadsResponse

    // ✅ Updated to accept List<String> for status
    suspend fun getLeadsByStatus(
        authToken: String,
        status: List<String>? = null,
        partType: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse
}