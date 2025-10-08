package com.rego.screens.orderdetails

import com.rego.screens.base.ViewEvent

sealed class OrderDetailsEvent : ViewEvent {
    object Init : OrderDetailsEvent()
    data class LoadOrders(val partTypeIndex: Int) : OrderDetailsEvent()
    data class SelectOrder(val orderId: String) : OrderDetailsEvent()
    data class LoadOrderDetails(val orderId: String) : OrderDetailsEvent()
}
