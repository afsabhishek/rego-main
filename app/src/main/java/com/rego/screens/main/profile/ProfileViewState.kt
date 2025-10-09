package com.rego.screens.main.profile

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState

@Immutable
data class ProfileViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val customerId: String? = null,
    val city: String? = null,
    val state: String? = null,
    val insuranceCompany: String? = null,
    val role: String? = null,
    val error: String? = null
) : ViewState