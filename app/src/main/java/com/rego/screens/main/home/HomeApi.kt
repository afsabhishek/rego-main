package com.rego.screens.main.home

import com.rego.screens.main.home.data.LeadStatsResponse

interface HomeApi {
    suspend fun getLeadStats(authToken: String): LeadStatsResponse
}