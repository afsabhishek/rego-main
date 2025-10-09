package com.rego.screens.main.home

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.components.OrderData
import com.rego.screens.main.home.data.LeadStatsResponse

@Immutable
data class HomeViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val summaryCards: List<Triple<String, Int, Int>>? = null,
    val ongoingOrdersAll: List<OrderData>? = null,
    val ongoingOrdersFiltered: List<OrderData>? = null,
    val quickFilters: List<String>? = null,
    val userName: String? = null,
    val userInitial: String = "U",
    val leadStats: LeadStatsResponse.LeadStats? = null,
    val selectedFilter: String? = null,
    val error: String? = null
) : ViewState