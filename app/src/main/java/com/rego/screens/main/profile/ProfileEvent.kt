package com.rego.screens.main.profile

import com.rego.screens.base.ViewEvent

// Update ProfileEvent to include Logout
sealed class ProfileEvent : ViewEvent {
    object Init : ProfileEvent()
    object Refresh : ProfileEvent()
    object Logout : ProfileEvent()
}