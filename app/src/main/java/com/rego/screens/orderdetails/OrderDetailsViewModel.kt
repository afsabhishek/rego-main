package com.rego.screens.orderdetails

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import kotlinx.coroutines.launch

class OrderDetailsViewModel(private val interactor: OrderDetailsInteractor) :
    BaseViewModel<OrderDetailsEvent, OrderDetailsViewState, OrderDetailsAction>() {

    override fun setInitialState() = OrderDetailsViewState()

    override fun onTriggerEvent(event: OrderDetailsEvent) {
        when (event) {
            is OrderDetailsEvent.Init -> {
                loadOrders(0)
            }

            is OrderDetailsEvent.LoadOrders -> {
                loadOrders(event.partTypeIndex)
            }

            is OrderDetailsEvent.SelectOrder -> {
                setState { copy(selectedOrderId = event.orderId) }
            }

            is OrderDetailsEvent.LoadOrderDetails -> {
                loadOrderDetails(event.orderId)
            }
        }
    }

    private fun loadOrders(type: Int) {
        viewModelScope.launch {
            interactor.getOrderList().collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> setState { copy(progressBarState = dataState.progressBarState) }
                    is DataState.Data -> setState {
                        copy(
                            orderListByType = dataState.data,
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            interactor.getOrderDetails(orderId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> setState { copy(progressBarState = dataState.progressBarState) }
                    is DataState.Data -> setState {
                        copy(
                            selectedOrderDetails = dataState.data,
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }
}
