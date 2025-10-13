package com.rego.screens.joinus

import com.rego.CommonResponse
import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.joinus.data.JoinUsResponse
import com.rego.screens.joinus.data.RegisterReferenceResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class JoinUsApiImpl(private val ktorClient: KtorClient) : JoinUsApi {

    override suspend fun getRegisterReference(): RegisterReferenceResponse {
        return try {
            println("📡 Fetching register reference data...")

            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.INSURANCE_COMPANIES}")
                // Explicitly remove auth header for this public endpoint
                headers {
                    remove(HttpHeaders.Authorization)
                }
            }

            val result = response.body<RegisterReferenceResponse>()

            // ✅ Debug logging
            println("✅ Register Reference Response:")
            println("  Insurance Companies: ${result.data?.insuranceCompanies?.size ?: 0}")
            println("  States: ${result.data?.states?.size ?: 0}")
            println("  State-City Mappings: ${result.data?.stateCityMapping?.size ?: 0}")

            result
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Failed to fetch register reference: ${e.message}")

            RegisterReferenceResponse(
                success = false,
                data = null,
                message = "Connection failed: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun submitJoinUsRequest(
        request: JoinUsRequest
    ): CommonResponse<JoinUsResponse.JoinUsData> {
        return try {
            println("📝 Submitting join us request...")
            println("  Name: ${request.name}")
            println("  Email: ${request.email}")
            println("  State: ${request.state}")
            println("  City: ${request.city}")
            println("  Insurance Company: ${request.insuranceCompany}")

            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.JOIN_US_REGISTER}")
                contentType(ContentType.Application.Json)
                // ✅ Explicitly remove auth header for signup
                headers {
                    remove(HttpHeaders.Authorization)
                }
                setBody(request)
            }

            val joinUsResponse = response.body<JoinUsResponse>()

            CommonResponse(
                data = joinUsResponse.data,
                status = joinUsResponse.success,
                message = joinUsResponse.data?.message
            )

        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Join us request failed: ${e.message}")

            CommonResponse(
                data = null,
                status = false,
                message = "Registration failed: ${e.localizedMessage}"
            )
        }
    }
}