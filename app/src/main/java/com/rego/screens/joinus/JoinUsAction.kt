package com.rego.screens.joinus

import com.rego.screens.base.UIComponent
import com.rego.screens.base.ViewSingleAction
import com.rego.screens.joinus.data.JoinUsResponse

sealed class JoinUsAction : ViewSingleAction {
    data class ShowDialog(val uiComponent: UIComponent.Dialog) : JoinUsAction()
    data class ShowErrorScreen(val uiComponent: UIComponent.ErrorData) : JoinUsAction()
    data class ShowSnackbar(val uiComponent: UIComponent.Snackbar) : JoinUsAction()
    data class RegistrationSuccess(val data: JoinUsResponse.JoinUsData) : JoinUsAction()
}