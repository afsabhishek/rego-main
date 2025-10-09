package com.rego.screens.auth

import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthApiImpl(
    private val ktorClient: KtorClient
) : AuthApi {
    
    override suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        return try {
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}/auth/refresh-token")
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken = refreshToken))
            }
            
            response.body<RefreshTokenResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            RefreshTokenResponse(
                success = false,
                data = null,
                message = "Failed to refresh token: ${e.localizedMessage}"
            )
        }
    }
    
    override suspend fun validateToken(authToken: String): Boolean {
        return try {
            // Make a simple API call with the token to check if it's valid
            // Using profile endpoint as a validation check
            val response: HttpResponse = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}/user/profile")
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
            
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }
}