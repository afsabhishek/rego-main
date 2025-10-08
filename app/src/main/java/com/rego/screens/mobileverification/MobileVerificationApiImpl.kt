package com.rego.screens.mobileverification

import com.rego.CommonResponse
import com.rego.network.ApiRoutes
import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.mobileverification.data.LoginRequest
import com.rego.screens.mobileverification.data.OtpResponse
import com.rego.screens.mobileverification.data.VerifyOtpRequest
import com.rego.screens.mobileverification.data.VerifyOtpResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MobileVerificationApiImpl(private val ktorClient: KtorClient) : MobileVerificationApi {

    override suspend fun requestOtp(phoneNumber: String): CommonResponse<OtpResponse.Data> {
        return try {
            // Make API call - no client-side validation
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.AUTH_LOGIN}")
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(phoneNumber = phoneNumber))
            }

            // Parse backend response
            val otpResponse = response.body<OtpResponse>()

            CommonResponse(
                data = otpResponse.data,
                status = otpResponse.responseStatus,
                message = otpResponse.data?.message
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

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): CommonResponse<VerifyOtpResponse.LoginData> {
        return try {
            // Make API call - no client-side validation
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.AUTH_VERIFY_OTP}")
                contentType(ContentType.Application.Json)
                setBody(VerifyOtpRequest(
                    phoneNumber = phoneNumber,
                    otp = otp
                ))
            }

            // Parse backend response
            val verifyResponse = response.body<VerifyOtpResponse>()

            CommonResponse(
                data = verifyResponse.data,
                status = verifyResponse.success,
                message = verifyResponse.data?.message
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

    override suspend fun resendOtp(
        phoneNumber: String
    ): CommonResponse<OtpResponse.Data> {
        return try {
            // Make API call - no client-side validation
            val response = ktorClient.client.post {
                url("${NetworkConfig.BASE_URL}${ApiRoutes.AUTH_RESEND_OTP}")
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(phoneNumber = phoneNumber))
            }

            val resendResponse = response.body<OtpResponse>()

            CommonResponse(
                data = resendResponse.data,
                status = resendResponse.responseStatus,
                message = resendResponse.data?.message
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
}
