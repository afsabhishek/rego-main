package com.rego.screens.main.home

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.components.OrderData

@Immutable
data class HomeViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val summaryCards: List<Triple<String, Int, Int>>? = null,
    val ongoingOrdersAll: List<OrderData>? = null,
    val quickFilters: List<String>? = null,
) : ViewState