package com.rego.screens.orderdetails

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.components.OrderData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderDetailsInteractor(private val api: OrderDetailsApi) {
    fun getOrderList(): Flow<DataState<Map<Int, List<OrderData>>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val orders = api.getOrderList()
            emit(DataState.Data(orders))
        } catch (e: Exception) {
            emit(
                DataState.Error(
                    UIComponent.ErrorData("Failed", e.message ?: "Unknown Error", "Retry")
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getOrderDetails(orderId: String): Flow<DataState<OrderDetails>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val details = api.getOrderDetails(orderId)
            emit(DataState.Data(details))
        } catch (e: Exception) {
            emit(
                DataState.Error(
                    UIComponent.ErrorData("Failed", e.message ?: "Unknown Error", "Retry")
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
