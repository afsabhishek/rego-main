package com.rego.screens.orderdetails

import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.screens.orderdetails.data.LeadActionResponse
import com.rego.screens.orderdetails.data.OrderDetailsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class OrderDetailsApiImpl(
    private val ktorClient: KtorClient
) : OrderDetailsApi {

    override suspend fun getLeadById(authToken: String, _id: String): OrderDetailsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}/${_id}")
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
            response.body<OrderDetailsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            OrderDetailsResponse(
                success = false,
                data = null
            )
        }
    }

    override suspend fun getLeadsByStatus(
        authToken: String,
        status: List<String>?,  // ✅ Updated to List<String>
        partType: String?,
        page: Int,
        limit: Int
    ): LeadsResponse {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.GET_LEADS}")
                header(HttpHeaders.Authorization, "Bearer $authToken")

                // ✅ Add status filter - if list is not null and not empty
                status?.let { statusList ->
                    if (statusList.isNotEmpty()) {
                        println("📤 Adding status filters: $statusList")
                        statusList.forEach { s ->
                            parameter("status[]", s)
                        }
                    } else {
                        println("📤 Empty status list - fetching all statuses")
                    }
                }

                // ✅ Add part type filter
                partType?.let {
                    println("📤 Adding partType filter: $it")
                    parameter("partType", it)
                }
            }

            println("📤 API Response: ${response.status}")
            response.body<LeadsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ API Error: ${e.message}")
            LeadsResponse(
                success = false,
                data = null,
                message = "Failed to fetch leads: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun acceptLead(authToken: String, leadId: String): LeadActionResponse {
        return try {
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}/leads/accept")
                header(HttpHeaders.Authorization, "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(mapOf("leadId" to leadId))
            }
            response.body<LeadActionResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadActionResponse(
                success = false,
                message = "Failed to accept lead: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun rejectLead(authToken: String, leadId: String): LeadActionResponse {
        return try {
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}/leads/reject")
                header(HttpHeaders.Authorization, "Bearer $authToken")
                contentType(ContentType.Application.Json)
                setBody(mapOf("leadId" to leadId))
            }
            response.body<LeadActionResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            LeadActionResponse(
                success = false,
                message = "Failed to reject lead: ${e.localizedMessage}"
            )
        }
    }
}