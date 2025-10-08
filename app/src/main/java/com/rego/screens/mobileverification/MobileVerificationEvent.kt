package com.rego.screens.mobileverification

import com.rego.screens.base.ViewEvent

sealed class MobileVerificationEvent : ViewEvent {
    data class MobileNumberChanged(val value: String) : MobileVerificationEvent()
    object GetOtp : MobileVerificationEvent()
    data class OtpChanged(val value: String) : MobileVerificationEvent()
    object VerifyOtp : MobileVerificationEvent()
    object ResendOtp : MobileVerificationEvent()
    object Init : MobileVerificationEvent()
}
