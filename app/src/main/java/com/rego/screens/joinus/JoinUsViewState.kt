package com.rego.screens.joinus

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.joinus.data.InsuranceCompany
import com.rego.screens.joinus.data.JoinUsResponse

@Immutable
data class JoinUsViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,

    // Insurance companies
    val insuranceCompanies: List<InsuranceCompany> = emptyList(),
    val selectedCompany: InsuranceCompany? = null,

    // States and cities
    val states: List<String> = emptyList(),
    val selectedState: String = "",
    val stateCityMapping: Map<String, List<String>> = emptyMap(),
    val availableCities: List<String> = emptyList(),
    val selectedCity: String = "",

    // Registration result
    val registrationSuccess: JoinUsResponse.JoinUsData? = null
) : ViewState