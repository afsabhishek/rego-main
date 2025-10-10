package com.rego.screens.main.home

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.components.OrderData
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadsResponse

@Immutable
data class HomeViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val summaryCards: List<Triple<String, Int, Int>>? = null,
    val ongoingOrdersAll: List<OrderData>? = null,
    val ongoingOrdersFiltered: List<OrderData>? = null,
    val quickFilters: List<String>? = null,
    val userName: String? = null,
    val userInitial: String = "U",
    val leadStatsItems: List<LeadStatsResponse.LeadStatItem>? = null,
    val selectedFilter: String? = null,
    val error: String? = null,

    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val leads: List<LeadsResponse.LeadsData.Lead> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<OrderData>? = null,
    val pagination: LeadsResponse.LeadsData.Pagination? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = false,
    val statsError: String? = null,
    val leadsError: String? = null
) : ViewState {
    val displayOrders: List<OrderData>
        get() = when {
            searchResults != null -> searchResults
            ongoingOrdersFiltered != null -> ongoingOrdersFiltered
            else -> ongoingOrdersAll ?: emptyList()
        }
}