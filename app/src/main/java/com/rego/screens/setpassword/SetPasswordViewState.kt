package com.rego.screens.setpassword

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState

@Immutable
data class SetPasswordViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordSet: Boolean? = null,
    val error: String? = null
) : ViewState
