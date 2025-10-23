package com.rego.screens.orderdetails

import com.rego.screens.base.ViewSingleAction

sealed class OrderDetailsAction : ViewSingleAction {
    data class LeadAccepted(val leadId: String) : OrderDetailsAction()
    data class LeadRejected(val leadId: String) : OrderDetailsAction()
}
