package com.rego.screens.joinus

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.joinus.data.InsuranceCompany
import com.rego.screens.joinus.data.JoinUsResponse

@Immutable
data class JoinUsViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val insuranceCompanies: List<InsuranceCompany> = emptyList(),
    val selectedCompany: InsuranceCompany? = null,
    val registrationSuccess: JoinUsResponse.JoinUsData? = null
) : ViewState