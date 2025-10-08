package com.rego.screens.orderdetails

import com.rego.screens.components.OrderData

interface OrderDetailsApi {
    suspend fun getOrderList(): Map<Int, List<OrderData>>
    suspend fun getOrderDetails(orderId: String): OrderDetails
}
