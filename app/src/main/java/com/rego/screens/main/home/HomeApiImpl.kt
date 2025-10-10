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
                data = null,
                message = "Failed to fetch lead stats: ${e.localizedMessage}"
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

                // Add query parameters
                status?.let { parameter("status", it) }
                partType?.let { parameter("partType", it) }
                registrationNumber?.let { parameter("registrationNumber", it) }
                claimNumber?.let { parameter("claimNumber", it) }
                parameter("page", page)
                parameter("limit", limit)
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

    override suspend fun getLeadCounts(authToken: String): Map<String, Int> = coroutineScope {
        val statuses = listOf(
            LeadStatus.NEW,
            LeadStatus.APPROVED,
            LeadStatus.NOT_REPAIRABLE,
            LeadStatus.COMPLETED,
            LeadStatus.WORK_IN_PROGRESS,
            LeadStatus.PICKUP_ALIGNED,
            LeadStatus.PART_DELIVERED,
            LeadStatus.PICKUP_DONE,
            LeadStatus.INVOICE_GENERATED,
            LeadStatus.READY_FOR_DELIVERY
        )

        val counts = mutableMapOf<String, Int>()

        try {
            // Make parallel requests for each status with minimal delay to avoid overwhelming server
            val deferredResults = statuses.map { status ->
                async {
                    delay(50) // Small delay between requests
                    try {
                        val response = getLeads(
                            authToken = authToken,
                            status = status.value,
                            partType = null,
                            registrationNumber = null,
                            claimNumber = null,
                            page = 1,
                            limit = 1 // We only need the count from pagination
                        )
                        if (response.success) {
                            status.value to (response.data?.pagination?.total ?: 0)
                        } else {
                            status.value to 0
                        }
                    } catch (e: Exception) {
                        println("Error fetching count for ${status.value}: ${e.message}")
                        status.value to 0
                    }
                }
            }

            // Collect all results
            deferredResults.forEach { deferred ->
                val (status, count) = deferred.await()
                counts[status] = count
            }

            // Also get total count without status filter
            val totalDeferred = async {
                try {
                    val totalResponse = getLeads(
                        authToken = authToken,
                        status = null,
                        partType = null,
                        registrationNumber = null,
                        claimNumber = null,
                        page = 1,
                        limit = 1
                    )
                    if (totalResponse.success) {
                        totalResponse.data?.pagination?.total ?: 0
                    } else {
                        counts.values.sum()
                    }
                } catch (e: Exception) {
                    counts.values.sum()
                }
            }

            counts["TOTAL"] = totalDeferred.await()

        } catch (e: Exception) {
            println("Error in getLeadCounts: ${e.message}")
            // Return empty counts on error
        }

        counts
    }

    override suspend fun searchLeads(
        authToken: String,
        query: String,
        page: Int,
        limit: Int
    ): LeadsResponse {
        return try {
            // Search in registration number and claim number
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")

                // Use query for both registration and claim number search
                if (query.isNotBlank()) {
                    // You might need to adjust based on your backend's search implementation
                    parameter("search", query) // Generic search parameter
                    // OR use specific fields
                    // parameter("registrationNumber", query)
                    // parameter("claimNumber", query)
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