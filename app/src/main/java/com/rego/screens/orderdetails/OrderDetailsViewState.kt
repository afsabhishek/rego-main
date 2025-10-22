package com.rego.screens.orderdetails

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.screens.orderdetails.data.OrderDetailsResponse

@Immutable
data class OrderDetailsViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val selectedId: String? = null,
    val selectedLead: OrderDetailsResponse? = null,

    // ✅ Updated to List<String>
    val currentStatus: List<String>? = null,
    val currentPartType: String? = null,
    val currentPage: Int = 1,

    val leads: List<LeadsResponse.LeadsData.Lead> = emptyList(),
    val error: String? = null
) : ViewState