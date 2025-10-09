package com.rego.screens.main.home

import androidx.lifecycle.viewModelScope
import com.rego.R
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.OrderData
import com.rego.screens.main.profile.ProfileInteractor
import com.rego.util.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
    private val profileInteractor: ProfileInteractor,
    private val userPreferences: UserPreferences
) : BaseViewModel<HomeEvent, HomeViewState, HomeAction>() {

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
                copy(progressBarState = ProgressBarState.Loading)
            }

            // Load user profile
            loadUserProfile()

            // Load home data
            delay(500)
            setState {
                copy(
                    quickFilters = getQuickFilters(),
                    summaryCards = getSummaryCards(),
                    ongoingOrdersAll = getOngoingOrdersAll()
                )
            }
            setState {
                copy(progressBarState = ProgressBarState.Idle)
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            profileInteractor.getUserProfile().collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { profile ->
                            setState {
                                copy(
                                    userName = profile.name,
                                    userInitial = profile.name.firstOrNull()?.toString() ?: "U"
                                )
                            }
                        }
                    }
                    is DataState.Error -> {
                        // Handle error if needed, but don't block home screen
                        setError { dataState.uiComponent }
                    }
                    else -> {}
                }
            }
        }
    }

    // --- Sample data methods remain the same ---
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