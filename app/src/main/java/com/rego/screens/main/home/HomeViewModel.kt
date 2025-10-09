package com.rego.screens.main.home
import androidx.lifecycle.viewModelScope
import com.rego.R
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.profile.ProfileInteractor
import com.rego.util.UserPreferences
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
            is HomeEvent.RefreshData -> {
                refreshData()
            }
            is HomeEvent.FilterLeads -> {
                filterLeadsByStatus(event.status)
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

            // Load lead statistics
            loadLeadStats()

            // Load ongoing leads
//            loadOngoingLeads()
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            setState {
                copy(progressBarState = ProgressBarState.Refreshing)
            }


            loadLeadStats()
            /* // Reload stats and leads
            loadOngoingLeads()*/
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
                                    userInitial = profile.name.firstOrNull()?.toString()?.uppercase() ?: "U"
                                )
                            }
                        }
                    }
                    is DataState.Error -> {
                        // Handle error silently for profile
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadLeadStats() {
        viewModelScope.launch {
            homeInteractor.getLeadStats().collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        if (state.value.progressBarState != ProgressBarState.Refreshing) {
                            setState { copy(progressBarState = dataState.progressBarState) }
                        }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { stats ->
                            setState {
                                copy(
                                    summaryCards = createSummaryCards(stats),
                                    quickFilters = getQuickFilters(),
                                    leadStats = stats,
                                    progressBarState = ProgressBarState.Idle
                                )
                            }
                        }
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

    /*
    private fun loadOngoingLeads(status: String? = null) {
        viewModelScope.launch {
            homeInteractor.getLeadsList(
                status = status,
                page = 1,
                limit = 20
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { leads ->
                            setState {
                                copy(
                                    ongoingOrdersAll = mapLeadsToOrderData(leads),
                                    ongoingOrdersFiltered = if (status != null) {
                                        mapLeadsToOrderData(leads.filter { it.status == status })
                                    } else {
                                        mapLeadsToOrderData(leads)
                                    }
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        // Handle error silently for leads list
                    }

                    else -> {}
                }
            }
        }
    }*/

    private fun filterLeadsByStatus(status: String?) {
        if (status == null) {
            // Show all orders
            setState {
                copy(
                    ongoingOrdersFiltered = ongoingOrdersAll,
                    selectedFilter = null
                )
            }
        } else {
            // Filter by status
            setState {
                copy(
                    ongoingOrdersFiltered = ongoingOrdersAll?.filter { it.status == status },
                    selectedFilter = status
                )
            }
        // Optionally load filtered data from API
        // loadOngoingLeads(status)
        }
    }

    private fun createSummaryCards(stats: LeadStatsResponse.LeadStats): List<Triple<String, Int, Int>> {
        return listOf(
            Triple("New Leads", R.drawable.audience, stats.newLeads),
            Triple("Total Leads", R.drawable.total_leads, stats.totalLeads),
            Triple("Approved", R.drawable.approved, stats.approved),
            Triple("Not Repairable", R.drawable.not_repairable, stats.notRepairable),
            Triple("Completed", R.drawable.completed, stats.completed),
            Triple("Pending", R.drawable.pending, stats.workInProgress)
        )
    }

    private fun getQuickFilters(): List<String> {
        return listOf(
            "Work In Progress",
            "Pickup Aligned",
            "Part Delivered",
            "Pickup Done",
            "Invoice Generated",
            "Ready for Delivery"
        )
    }

   /* private fun mapLeadsToOrderData(leads: List<Lead>): List<OrderData> {
        return leads.map { lead ->
            OrderData(
                orderId = lead.leadId,
                status = lead.status,
                carMake = "${lead.carMake} ${lead.carModel}, ${lead.makeYear}",
                deliveryDate = formatDate(lead.targetDeliveryDate),
                dealerName = lead.dealerName,
                dealerLocation = lead.dealerLocation
            )
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString == null) return "TBD"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "TBD"
        } catch (e: Exception) {
            "TBD"
        }
    }*/
}