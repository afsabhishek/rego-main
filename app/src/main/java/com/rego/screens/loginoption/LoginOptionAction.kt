package com.rego.screens.loginoption

import com.rego.screens.base.ViewSingleAction

sealed class LoginOptionAction : ViewSingleAction {
    object OnLoginClick : LoginOptionAction()
    object OnSignUpClick : LoginOptionAction()
}