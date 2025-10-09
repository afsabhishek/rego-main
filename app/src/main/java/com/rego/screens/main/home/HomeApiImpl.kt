package com.rego.screens.main.home

import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.main.home.data.LeadStatsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class HomeApiImpl(
    private val ktorClient: KtorClient
) : HomeApi {

    override suspend fun getLeadStats(authToken: String): LeadStatsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS_STATS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
            response.body<LeadStatsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadStatsResponse(
                success = false,
                data = null,
                message = "Failed to fetch lead stats: ${e.localizedMessage}"
            )
        }
    }
}