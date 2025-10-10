package com.rego.screens.orderdetails

import com.rego.screens.base.ViewEvent

sealed class OrderDetailsEvent : ViewEvent {
    object Init : OrderDetailsEvent()
    data class LoadLeadDetails(val leadId: String) : OrderDetailsEvent()
    data class LoadLeadsByStatus(val status: String?, val page: Int = 1) : OrderDetailsEvent()
    object RetryLoadDetails : OrderDetailsEvent()
    object LoadMoreLeads : OrderDetailsEvent()
}