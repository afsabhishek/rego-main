package com.rego.screens.main.profile

import com.rego.CommonResponse
import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.main.profile.data.ProfileResponse
import com.rego.util.UserPreferences
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class ProfileApiImpl(
    private val ktorClient: KtorClient,
    private val userPreferences: UserPreferences
) : ProfileApi {

    override suspend fun getUserProfile(authToken: String): ProfileResponse {
        return try {
            val freshToken = userPreferences.getAuthToken()

            val response = ktorClient.client.get {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.USER_PROFILE}")
                header(HttpHeaders.Authorization, "Bearer $freshToken")
            }
            response.body<ProfileResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            ProfileResponse(
                success = false,
                data = null,
                message = "Failed to fetch profile: ${e.localizedMessage}"
            )
        }
    }
}
