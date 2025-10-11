package com.rego.screens.raiserequest

import com.rego.screens.base.ViewSingleAction

// Updated Actions
sealed class RaiseRequestAction : ViewSingleAction {
    object NavigateToSuccess : RaiseRequestAction()
    data class ShowError(val message: String) : RaiseRequestAction()
}