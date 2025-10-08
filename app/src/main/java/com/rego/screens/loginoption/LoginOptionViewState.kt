package com.rego.screens.loginoption

import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState

data class LoginOptionViewState(val progressBarState: ProgressBarState = ProgressBarState.Idle) :
    ViewState