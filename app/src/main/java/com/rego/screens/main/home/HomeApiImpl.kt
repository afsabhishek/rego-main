package com.rego.screens.main.home

import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadStatus
import com.rego.screens.main.home.data.LeadsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
                data = null
            )
        }
    }

    override suspend fun getLeads(
        authToken: String,
        status: String?,
        partType: String?,
        registrationNumber: String?,
        claimNumber: String?,
        page: Int,
        limit: Int
    ): LeadsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")

                status?.let { parameter("status", it) }
                partType?.let { parameter("partType", it) }
                registrationNumber?.let { parameter("registrationNumber", it) }
                claimNumber?.let { parameter("claimNumber", it) }
            }

            response.body<LeadsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadsResponse(
                success = false,
                data = null,
                message = "Failed to fetch leads: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun searchLeads(
        authToken: String,
        query: String,
        page: Int,
        limit: Int
    ): LeadsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")

                if (query.isNotBlank()) {
                    parameter("search", query)
                }
                parameter("page", page)
                parameter("limit", limit)
            }

            response.body<LeadsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadsResponse(
                success = false,
                data = null,
                message = "Search failed: ${e.localizedMessage}"
            )
        }
    }
}
