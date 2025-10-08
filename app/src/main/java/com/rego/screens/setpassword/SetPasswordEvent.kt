package com.rego.screens.setpassword

import com.rego.screens.base.ViewEvent

sealed class SetPasswordEvent : ViewEvent {
    object Init : SetPasswordEvent()
    data class PasswordChanged(val password: String) : SetPasswordEvent()
    data class ConfirmPasswordChanged(val password: String) : SetPasswordEvent()
    data class SetPassword(val userId: String, val password: String) : SetPasswordEvent()
}
