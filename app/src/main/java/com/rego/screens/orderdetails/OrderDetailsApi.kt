package com.rego.screens.orderdetails

import com.rego.screens.main.home.data.LeadsResponse
import com.rego.screens.orderdetails.data.OrderDetailsResponse
import com.rego.screens.orderdetails.data.LeadActionResponse

interface OrderDetailsApi {
    suspend fun getLeadById(authToken: String, _id: String): OrderDetailsResponse

    suspend fun getLeadsByStatus(
        authToken: String,
        status: List<String>? = null,
        partType: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse

    // New methods for lead actions
    suspend fun acceptLead(authToken: String, leadId: String): LeadActionResponse
    suspend fun rejectLead(authToken: String, leadId: String): LeadActionResponse
}