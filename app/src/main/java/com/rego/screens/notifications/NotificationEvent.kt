package com.rego.screens.notifications

import com.rego.screens.base.ViewEvent

sealed class NotificationEvent : ViewEvent {
    object Init : NotificationEvent()
    data class NotificationClicked(val notification: NotificationItem) : NotificationEvent()
    // Add more events as needed
}
