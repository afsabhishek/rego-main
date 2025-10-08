package com.rego.screens.main.profile

import com.rego.screens.base.ViewEvent

sealed class ProfileEvent : ViewEvent {
    object Init : ProfileEvent()
}