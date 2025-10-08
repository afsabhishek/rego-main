package com.rego.screens.main.home

import androidx.lifecycle.viewModelScope
import com.rego.R
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.OrderData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val homeInteractor: HomeInteractor) :
    BaseViewModel<HomeEvent, HomeViewState, HomeAction>() {

    override fun setInitialState() = HomeViewState()

    override fun onTriggerEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> {
                init()
            }
        }
    }

    private fun init() {
        viewModelScope.launch {
            setState {
                copy(
                    progressBarState = ProgressBarState.Loading
                )
            }
            delay(500)
            setState {
                copy(
                    quickFilters = getQuickFilters(),
                    summaryCards = getSummaryCards(),
                    ongoingOrdersAll = getOngoingOrdersAll()
                )
            }
            setState {
                copy(
                    progressBarState = ProgressBarState.Idle
                )
            }
            return@launch
            /**
             * future approach
             */
            homeInteractor.getData().collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {

                    }

                    is DataState.Data -> {

                    }

                    is DataState.Error -> {

                    }

                    is DataState.NetworkStatus -> {

                    }
                }
            }
        }
    }

    // --- Sample data ---
    fun getQuickFilters() = listOf(
        "Work In Progress",
        "Pickup Aligned",
        "Part Delivered",
        "Pickup Done",
        "Invoice Generated",
        "Ready for Delivery"
    )

    fun getSummaryCards() = listOf(
        Triple("New Leads", R.drawable.audience, 0),
        Triple("Total Leads", R.drawable.total_leads, 0),
        Triple("Approved", R.drawable.approved, 1),
        Triple("Not Repairable", R.drawable.not_repairable, 0),
        Triple("Completed", R.drawable.completed, 1),
        Triple("Pending", R.drawable.pending, 0)
    )

    // Extended sample for ALL statuses
    fun getOngoingOrdersAll() = listOf(
        OrderData(
            orderId = "12042501",
            status = "Pickup Aligned",
            carMake = "Hyundai i20, 2023",
            deliveryDate = "21/04/25"
        ),
        OrderData(
            orderId = "13042512",
            status = "Pickup Aligned",
            carMake = "Honda City, 2020",
            deliveryDate = "21/02/24"
        ),
        OrderData(
            orderId = "18049231",
            status = "Work In Progress",
            carMake = "Honda City, 2020",
            deliveryDate = "21/02/24"
        ),
        OrderData(
            orderId = "19002451",
            status = "Pickup Done",
            carMake = "Hyundai Verna, 2021",
            deliveryDate = "22/07/24"
        ),
        OrderData(
            orderId = "11049501",
            status = "Part Delivered",
            carMake = "MG Hector, 2022",
            deliveryDate = "29/11/24"
        ),
        OrderData(
            orderId = "12011111",
            status = "Invoice Generated",
            carMake = "Maruti Alto, 2022",
            deliveryDate = "01/12/24"
        ),
        OrderData(
            orderId = "19005001",
            status = "Ready for Delivery",
            carMake = "Suzuki Baleno, 2021",
            deliveryDate = "10/10/24"
        )
    )

}