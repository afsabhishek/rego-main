package com.rego.screens.main.home

import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadsResponse

interface HomeApi {
    suspend fun getLeadStats(authToken: String): LeadStatsResponse

    suspend fun getLeads(
        authToken: String,
        status: List<String>? = null,  // ✅ Changed to List<String>
        partType: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse

    suspend fun searchLeads(
        authToken: String,
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse
}