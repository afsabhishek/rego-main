package com.rego.screens.orderdetails

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.main.home.data.LeadsResponse

@Immutable
data class OrderDetailsViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val selectedLeadId: String? = null,
    val selectedLead: LeadsResponse.LeadsData.Lead? = null,

    // ✅ Updated to List<String>
    val currentStatus: List<String>? = null,
    val currentPartType: String? = null,
    val currentPage: Int = 1,

    val leads: List<LeadsResponse.LeadsData.Lead> = emptyList(),
    val pagination: LeadsResponse.LeadsData.Pagination? = null,
    val hasMorePages: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
) : ViewState