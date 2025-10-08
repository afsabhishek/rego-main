package com.rego.screens.mobileverification

import com.rego.CommonResponse
import com.rego.screens.mobileverification.data.OtpResponse
import com.rego.screens.mobileverification.data.VerifyOtpResponse

interface MobileVerificationApi {
    suspend fun requestOtp(phoneNumber: String): CommonResponse<OtpResponse.Data>
    suspend fun verifyOtp(phoneNumber: String, otp: String): CommonResponse<VerifyOtpResponse.LoginData>
    suspend fun resendOtp(phoneNumber: String): CommonResponse<OtpResponse.Data>
}