package com.rego.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkConfig {
    const val BASE_URL = "https://rego-backend-staging-s3yjdx63fa-uc.a.run.app/api" // Replace with your actual domain
}

object ApiRoutes {
    const val AUTH_LOGIN = "/auth/login"
    const val AUTH_VERIFY_OTP = "/auth/verify-login-otp"
    const val AUTH_RESEND_OTP = "/auth/resend-otp"
    const val INSURANCE_COMPANIES = "/insurance-companies"
    const val JOIN_US_REGISTER = "/auth/signup"
    const val USER_PROFILE = "/user/profile"
}

class KtorClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
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