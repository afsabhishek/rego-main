package com.rego.screens.orderdetails

import com.rego.screens.base.ViewEvent

sealed class OrderDetailsEvent : ViewEvent {
    object Init : OrderDetailsEvent()
    data class LoadLeadDetails(val _id: String) : OrderDetailsEvent()
    data class LoadLeadsByStatus(val status: List<String>? = null, val page: Int = 1) : OrderDetailsEvent()
    object RetryLoadDetails : OrderDetailsEvent()

    // New events for Accept and Reject
    data class AcceptLead(val leadId: String) : OrderDetailsEvent()
    data class RejectLead(val leadId: String) : OrderDetailsEvent()
}