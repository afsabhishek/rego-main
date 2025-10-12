package com.rego.network

import com.rego.util.UserPreferences
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkConfig {
    const val BASE_URL = "https://rego-backend-staging-s3yjdx63fa-uc.a.run.app/api"
}

object ApiRoutes {
    const val AUTH_LOGIN = "/auth/login"
    const val AUTH_VERIFY_OTP = "/auth/verify-login-otp"
    const val AUTH_RESEND_OTP = "/auth/resend-otp"
    const val AUTH_REFRESH_TOKEN = "/auth/refresh-token"
    const val INSURANCE_COMPANIES = "/insurance-companies"
    const val JOIN_US_REGISTER = "/auth/signup"
    const val USER_PROFILE = "/user/profile"
    const val GET_LEADS_STATS = "/leads/stats"
    const val GET_LEADS = "/leads"
    const val VEHICLES_MAKES = "/vehicles/makes"
    const val VEHICLES_MODELS = "/vehicles/models"
    const val VEHICLES_VARIANTS = "/vehicles/variants"
    const val WORKSHOPS_LOCATIONS = "/workshops/locations"
    const val WORKSHOPS_DEALERS = "/workshops/dealers"
    const val REFERENCE_PARTS = "/reference/parts"
    const val LEADS = "/leads"
}

class KtorClient(
    private val userPreferences: UserPreferences? = null
) {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        // Add Auth plugin for automatic token handling
        if (userPreferences != null) {
            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from preferences
                        val accessToken = userPreferences.getAuthToken()
                        val refreshToken = userPreferences.getRefreshToken()
                        if (accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        // Refresh token logic
                        val refreshToken = userPreferences.getRefreshToken()
                        if (refreshToken != null) {
                            try {
                                // Make refresh token API call
                                val response: io.ktor.client.statement.HttpResponse = client.post {
                                    url("${NetworkConfig.BASE_URL}${ApiRoutes.AUTH_REFRESH_TOKEN}")
                                    contentType(ContentType.Application.Json)
                                    setBody(mapOf("refreshToken" to refreshToken))
                                }

                                if (response.status == HttpStatusCode.OK) {
                                    val tokenResponse: com.rego.screens.auth.RefreshTokenResponse =
                                        response.body()

                                    if (tokenResponse.success && tokenResponse.data != null) {
                                        // Save new tokens
                                        userPreferences.saveAuthToken(
                                            tokenResponse.data.authToken,
                                            tokenResponse.data.expiresIn
                                        )
                                        userPreferences.saveRefreshToken(tokenResponse.data.refreshToken)

                                        BearerTokens(
                                            tokenResponse.data.authToken,
                                            tokenResponse.data.refreshToken
                                        )
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        } else {
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        // Don't add auth header for login/signup endpoints
                        !request.url.pathSegments.any {
                            it.contains("login") ||
                                    it.contains("signup") ||
                                    it.contains("verify-otp") ||
                                    it.contains("resend-otp") ||
                                    it.contains("refresh-token")
                        }
                    }
                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("Ktor Log: $message")
                }
            }
            level = LogLevel.ALL
        }

        install(ResponseObserver) {
            onResponse { response ->
                println("HTTP status: ${response.status.value}")

                // Handle 401 Unauthorized globally
                if (response.status == HttpStatusCode.Unauthorized) {
                    // Token might be expired, will be handled by Auth plugin
                    println("Unauthorized - token might be expired")
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }

        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
    }
}

// Extension function to add Firebase ID token header
suspend fun HttpRequestBuilder.addFirebaseIdToken(userPreferences: UserPreferences) {
    val firebaseToken = userPreferences.getFirebaseIdToken()
    if (!firebaseToken.isNullOrEmpty()) {
        header("X-Firebase-Token", firebaseToken)
    }
}

// Extension function to add both backend and Firebase tokens
suspend fun HttpRequestBuilder.addAllAuthHeaders(userPreferences: UserPreferences) {
    val backendToken = userPreferences.getAuthToken()
    if (!backendToken.isNullOrEmpty()) {
        header(HttpHeaders.Authorization, "Bearer $backendToken")
    }

    val firebaseToken = userPreferences.getFirebaseIdToken()
    if (!firebaseToken.isNullOrEmpty()) {
        header("X-Firebase-Token", firebaseToken)
    }
}