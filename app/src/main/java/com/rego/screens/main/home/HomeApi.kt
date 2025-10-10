package com.rego.screens.main.home

import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadsResponse

interface HomeApi {
    suspend fun getLeadStats(authToken: String): LeadStatsResponse
    suspend fun getLeads(
        authToken: String,
        status: String? = null,
        partType: String? = null,
        registrationNumber: String? = null,
        claimNumber: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse

    // Get counts for each status (for cards)
    suspend fun getLeadCounts(authToken: String): Map<String, Int>

    // Search leads
    suspend fun searchLeads(
        authToken: String,
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): LeadsResponse
}