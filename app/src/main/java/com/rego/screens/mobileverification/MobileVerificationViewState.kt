package com.rego.screens.mobileverification

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
@Immutable
data class MobileVerificationViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val mobileNumber: String = "",
    val otp: String = "",
    val resendTimer: Int = 30,
    val isResendEnabled: Boolean = false,
    val isOtpRequested: Boolean = false,
    val isOtpVerified: Boolean = false,

    // From backend OTP response
    val backendMessage: String? = null,

    // From backend verify response - Authentication
    val authToken: String? = null,
    val refreshToken: String? = null,
    val tokenExpiresIn: Int = 3600,
    val tokenType: String? = null,

    // From backend verify response - User
    val userId: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userCity: String? = null,
    val userState: String? = null,
    val userInsuranceCompany: String? = null,
    val userRole: String? = null,

    // UI states
    val errorMessage: String? = null,
    val resendCount: Int = 0
) : ViewState

