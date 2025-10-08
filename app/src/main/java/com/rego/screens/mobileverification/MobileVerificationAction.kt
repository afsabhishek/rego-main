package com.rego.screens.mobileverification

import com.rego.screens.base.ViewSingleAction

sealed class MobileVerificationAction : ViewSingleAction {
    object NavigateToHome : MobileVerificationAction()
    object ShowOtpExpiredDialog : MobileVerificationAction()
}