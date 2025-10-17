package com.rego.screens.main.home

import androidx.lifecycle.viewModelScope
import com.rego.R
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.OrderData
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadStatus
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.screens.main.profile.ProfileInteractor
import com.rego.util.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(
    private val homeInteractor: HomeInteractor,
    private val profileInteractor: ProfileInteractor,
    private val userPreferences: UserPreferences
) : BaseViewModel<HomeEvent, HomeViewState, HomeAction>() {

    private var searchJob: Job? = null
    private var currentLeads = mutableListOf<LeadsResponse.LeadsData.Lead>()

    override fun setInitialState() = HomeViewState()

    override fun onTriggerEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Init -> init()
            is HomeEvent.RefreshData -> refreshData()
            is HomeEvent.FilterLeads -> filterLeadsByStatus(event.status)
            is HomeEvent.LoadMoreLeads -> loadMoreLeads()
            is HomeEvent.SearchLeads -> searchLeads(event.query)
            is HomeEvent.ClearSearch -> clearSearch()
            is HomeEvent.RetryLoadStats -> loadLeadStats()
            is HomeEvent.RetryLoadLeads -> loadOngoingLeads()
            is HomeEvent.OnCardClick -> handleCardClick(event.cardType)
            is HomeEvent.OnOrderClick -> handleOrderClick(event.orderId)
            is HomeEvent.OnProfileClick -> setAction { HomeAction.NavigateToProfile }
            is HomeEvent.OnNotificationClick -> setAction { HomeAction.NavigateToNotifications }
            is HomeEvent.OnRaiseRequestClick -> setAction { HomeAction.NavigateToRaiseRequest }
            is HomeEvent.OnSearchClick -> setAction { HomeAction.NavigateToSearch }
        }
    }

    private fun init() {
        viewModelScope.launch {
            setState { copy(progressBarState = ProgressBarState.Loading) }

            launch { loadUserProfile() }
            launch { loadLeadStats() }
            launch { loadOngoingLeads() }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            launch { loadLeadStats() }
            launch { loadOngoingLeads() }

            delay(500)
            setState { copy(isRefreshing = false) }
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
                    is DataState.Error -> {}
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
                        if (!state.value.isRefreshing) {
                            setState { copy(progressBarState = dataState.progressBarState) }
                        }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { stats ->
                            setState {
                                copy(
                                    summaryCards = createSummaryCardsFromStats(stats),
                                    quickFilters = getQuickFilters(),
                                    leadStatsItems = stats,
                                    progressBarState = ProgressBarState.Idle,
                                    statsError = null
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                progressBarState = ProgressBarState.Idle,
                                statsError = "Failed to load statistics"
                            )
                        }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadOngoingLeads(status: String? = null, page: Int = 1, append: Boolean = false) {
        viewModelScope.launch {
            // Default to WORK_IN_PROGRESS statuses
            val defaultStatuses = listOf(
                "PICKUP_ALIGNED",
                "PHYSICAL_INSPECTION_ALIGNED",
                "PICKUP_DONE",
                "WORK_IN_PROGRESS",
                "READY_FOR_DELIVERY",
                "INVOICE_GENERATED"
            )

            val searchStatus = status ?: defaultStatuses[3]

            homeInteractor.getLeadsList(
                status = searchStatus,
                page = page,
                limit = 20,
                showLoading = !append && !state.value.isRefreshing
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { leadsData ->
                            val newLeads = leadsData.leads

                            if (append) {
                                currentLeads.addAll(newLeads)
                            } else {
                                currentLeads.clear()
                                currentLeads.addAll(newLeads)
                            }

                            setState {
                                copy(
                                    leads = currentLeads.toList(),
                                    ongoingOrdersAll = mapLeadsToOrderData(currentLeads),
                                    ongoingOrdersFiltered = if (status != null) {
                                        mapLeadsToOrderData(currentLeads)
                                    } else {
                                        null
                                    },
                                    pagination = leadsData.pagination,
                                    currentPage = leadsData.pagination.currentPage,
                                    hasMorePages = leadsData.pagination.hasNextPage,
                                    isLoadingMore = false,
                                    leadsError = null
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                isLoadingMore = false,
                                leadsError = "Failed to load orders"
                            )
                        }
                        if (!append) {
                            setError { dataState.uiComponent }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadMoreLeads() {
        if (state.value.isLoadingMore || !state.value.hasMorePages) return

        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }
            val nextPage = state.value.currentPage + 1
            loadOngoingLeads(
                status = state.value.selectedFilter?.let { getStatusesForFilter(it) },
                page = nextPage,
                append = true
            )
        }
    }

    private fun filterLeadsByStatus(status: String?) {
        if (status == null) {
            setState {
                copy(
                    ongoingOrdersFiltered = null,
                    selectedFilter = null,
                    currentPage = 1
                )
            }
            loadOngoingLeads()
        } else {
            // Map the status key to API format
            val apiStatus = mapStatusKeyToApiFormat(status)

            setState {
                copy(
                    selectedFilter = status,
                    currentPage = 1
                )
            }

            loadOngoingLeads(apiStatus)
        }
    }

    /**
     * Maps UI status keys (from cards) to API status format
     */
    private fun mapStatusKeyToApiFormat(statusKey: String): String {
        return when (statusKey.uppercase()) {
            "NEW" -> "NEW"
            "TOTAL" -> "TOTAL" // Total leads shows WIP by default
            "APPROVED" -> "APPROVED"
            "REJECTED" -> "REJECTED"
            "DELIVERED" -> "DELIVERED"
            "WORK_IN_PROGRESS" -> "WORK_IN_PROGRESS"
            else -> statusKey.uppercase().replace(" ", "_")
        }
    }

    private fun searchLeads(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            clearSearch()
            return
        }

        setState { copy(searchQuery = query, isSearching = true) }

        searchJob = viewModelScope.launch {
            delay(300)

            homeInteractor.searchLeads(query).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { leadsData ->
                            setState {
                                copy(
                                    searchResults = mapLeadsToOrderData(leadsData.leads),
                                    isSearching = false
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                searchResults = emptyList(),
                                isSearching = false
                            )
                        }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun clearSearch() {
        searchJob?.cancel()
        setState {
            copy(
                searchQuery = "",
                searchResults = null,
                isSearching = false
            )
        }
    }

    private fun handleCardClick(cardType: String) {
        setAction { HomeAction.NavigateToOrderList(cardType) }

        // Load leads for the selected card type
        val statItem = state.value.leadStatsItems?.find { it.label == cardType }
        if (statItem != null && statItem.status.isNotEmpty()) {
            loadOngoingLeads(statItem.status.joinToString(","))
        } else if (cardType == "Total Leads") {
            loadOngoingLeads(null) // Load all leads
        }
    }

    private fun handleOrderClick(orderId: String) {
        setAction { HomeAction.NavigateToOrderDetails(orderId) }
    }

    private fun createSummaryCardsFromStats(stats: List<LeadStatsResponse.LeadStatItem>): List<Triple<String, Int, Int>> {
        val iconMap = mapOf(
            "New Leads" to R.drawable.audience,
            "Total Leads" to R.drawable.total_leads,
            "Approved" to R.drawable.approved,
            "Not Repairable" to R.drawable.not_repairable,
            "Completed" to R.drawable.completed,
            "Work in Progress" to R.drawable.pending
        )

        return stats.map { stat ->
            Triple(stat.label, iconMap[stat.label] ?: R.drawable.total_leads, stat.count)
        }
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

    private fun getStatusesForFilter(filterLabel: String): String {
        return when (filterLabel) {
            "Work In Progress" -> "WORK_IN_PROGRESS"
            "Pickup Aligned" -> "PICKUP_ALIGNED"
            "Part Delivered" -> "PART_DELIVERED"
            "Pickup Done" -> "PICKUP_DONE"
            "Invoice Generated" -> "INVOICE_GENERATED"
            "Ready for Delivery" -> "READY_FOR_DELIVERY"
            else -> filterLabel.uppercase().replace(" ", "_")
        }
    }

    private fun mapLeadsToOrderData(leads: List<LeadsResponse.LeadsData.Lead>): List<OrderData> {
        return leads.map { lead ->
            OrderData(
                orderId = lead.leadId,
                status = mapStatusToDisplay(lead.status),
                carMake = "${lead.vehicle.make} ${lead.vehicle.model}, ${lead.makeYear}",
                deliveryDate = formatDate(lead.activity.lastUpdatedAt),
                dealerName = lead.dealer.name,
                dealerLocation = lead.dealer.location
            )
        }
    }

    private fun mapStatusToDisplay(status: String): String {
        return when (status) {
            "NEW" -> "New"
            "APPROVED" -> "Approved"
            "REJECTED" -> "Not Repairable"
            "DELIVERED" -> "Completed"
            "WORK_IN_PROGRESS" -> "Work In Progress"
            "PICKUP_ALIGNED" -> "Pickup Aligned"
            "PART_DELIVERED" -> "Part Delivered"
            "PICKUP_DONE" -> "Pickup Done"
            "INVOICE_GENERATED" -> "Invoice Generated"
            "READY_FOR_DELIVERY" -> "Ready for Delivery"
            "PHYSICAL_INSPECTION_ALIGNED" -> "Physical Inspection Aligned"
            else -> status.replace("_", " ")
                .split(" ")
                .joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString == null) return "TBD"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "TBD"
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "TBD"
            } catch (e2: Exception) {
                "TBD"
            }
        }
    }
}