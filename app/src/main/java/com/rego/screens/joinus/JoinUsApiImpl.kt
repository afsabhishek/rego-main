package com.rego.screens.joinus

import com.rego.CommonResponse
import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.joinus.data.InsuranceCompaniesResponse
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.joinus.data.JoinUsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class JoinUsApiImpl(private val ktorClient: KtorClient) : JoinUsApi {

    override suspend fun getInsuranceCompanies(): CommonResponse<InsuranceCompaniesResponse.InsuranceCompaniesData> {
        return try {
            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.INSURANCE_COMPANIES}")
            }

            val insuranceResponse = response.body<InsuranceCompaniesResponse>()

            CommonResponse(
                data = insuranceResponse.data,
                status = insuranceResponse.responseStatus,
                message = if (insuranceResponse.data?.insuranceCompanies?.isNotEmpty() == true) {
                    "Insurance companies loaded successfully"
                } else {
                    "Failed to load insurance companies"
                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
            CommonResponse(
                data = null,
                status = false,
                message = "Connection failed: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun submitJoinUsRequest(
        request: JoinUsRequest
    ): CommonResponse<JoinUsResponse.JoinUsData> {
        return try {
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.JOIN_US_REGISTER}")
                contentType(ContentType.Application.Json)
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
            CommonResponse(
                data = null,
                status = false,
                message = "Registration failed: ${e.localizedMessage}"
            )
        }
    }
}