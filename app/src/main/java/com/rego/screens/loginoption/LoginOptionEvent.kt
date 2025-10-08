package com.rego.screens.loginoption

import com.rego.screens.base.ViewEvent

sealed class LoginOptionEvent : ViewEvent {
    object Init : LoginOptionEvent()
}