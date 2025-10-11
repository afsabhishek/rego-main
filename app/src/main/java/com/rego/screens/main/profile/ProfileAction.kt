package com.rego.screens.main.profile

import com.rego.screens.base.ViewSingleAction

// Update ProfileAction to include Logout navigation
sealed class ProfileAction : com.rego.screens.base.ViewSingleAction {
    object NavigateToLogin : ProfileAction()
    data class ShowError(val message: String) : ProfileAction()
}
