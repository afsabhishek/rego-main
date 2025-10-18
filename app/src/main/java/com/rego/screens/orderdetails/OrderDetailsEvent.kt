package com.rego.screens.orderdetails

import com.rego.screens.base.ViewEvent

sealed class OrderDetailsEvent : ViewEvent {
    object Init : OrderDetailsEvent()
    data class LoadLeadDetails(val leadId: String) : OrderDetailsEvent()

    // ✅ Updated to accept List<String> for status
    data class LoadLeadsByStatus(val status: List<String>? = null, val page: Int = 1) : OrderDetailsEvent()

    object RetryLoadDetails : OrderDetailsEvent()
    object LoadMoreLeads : OrderDetailsEvent()
}