package com.rego.screens.joinus

import com.rego.screens.base.ViewSingleAction
import com.rego.screens.joinus.data.JoinUsResponse

sealed class JoinUsAction : ViewSingleAction {
    data class RegistrationSuccess(val data: JoinUsResponse.JoinUsData) : JoinUsAction()
}