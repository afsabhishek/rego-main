package com.rego.screens.orderdetails

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.components.OrderData

@Immutable
data class OrderDetailsViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val orderListByType: Map<Int, List<OrderData>>? = null,
    val selectedOrderId: String? = null,
    val selectedOrderDetails: OrderDetails? = null
) : ViewState
