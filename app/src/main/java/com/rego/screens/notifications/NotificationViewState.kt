package com.rego.screens.notifications

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState

@Immutable
data class NotificationViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val notifications: List<NotificationItem>? = null
) : ViewState
