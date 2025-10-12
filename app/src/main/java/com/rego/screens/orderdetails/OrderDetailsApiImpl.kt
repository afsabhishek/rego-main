package com.rego.screens.orderdetails

import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.main.home.data.LeadsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class OrderDetailsApiImpl(
    private val ktorClient: KtorClient
) : OrderDetailsApi {

    override suspend fun getLeadById(authToken: String, leadId: String): LeadsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")
                parameter("leadId", leadId)
            }
            response.body<LeadsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadsResponse(
                success = false,
                data = null,
                message = "Failed to fetch lead details: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getLeadsByStatus(
        authToken: String,
        status: String?,
        page: Int,
        limit: Int
    ): LeadsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")
                status?.let { parameter("status", it) }
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
}