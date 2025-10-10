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

            // Load all data in parallel
            launch { loadUserProfile() }
            launch { loadLeadStats() }
            launch { loadOngoingLeads() }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            // Reload stats and leads
            launch { loadLeadStats() }
            launch { loadOngoingLeads() }

            delay(500) // Minimum refresh time for UX
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
                        if (!state.value.isRefreshing) {
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

            // Also load counts for more accurate card data
            loadLeadCounts()
        }
    }

    private fun loadLeadCounts() {
        viewModelScope.launch {
            homeInteractor.getLeadCounts().collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { counts ->
                            setState { copy(cardCounts = counts) }

                            // Update summary cards with actual counts
                            val stats = state.value.leadStats ?: LeadStatsResponse.LeadStats(
                                newLeads = counts[LeadStatus.NEW.value] ?: 0,
                                totalLeads = counts["TOTAL"] ?: 0,
                                approved = counts[LeadStatus.APPROVED.value] ?: 0,
                                notRepairable = counts[LeadStatus.NOT_REPAIRABLE.value] ?: 0,
                                completed = counts[LeadStatus.COMPLETED.value] ?: 0,
                                workInProgress = counts[LeadStatus.WORK_IN_PROGRESS.value] ?: 0
                            )
                            setState { copy(summaryCards = createSummaryCards(stats)) }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadOngoingLeads(status: String? = null, page: Int = 1, append: Boolean = false) {
        viewModelScope.launch {
            // Default to WORK_IN_PROGRESS if no status specified
            val searchStatus = status ?: LeadStatus.WORK_IN_PROGRESS.value

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
                                    currentPage = page,
                                    hasMorePages = leadsData.pagination.hasNext,
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
                status = state.value.selectedFilter?.let { mapDisplayToApiStatus(it) },
                page = nextPage,
                append = true
            )
        }
    }

    private fun filterLeadsByStatus(status: String?) {
        if (status == null) {
            // Show all Work in Progress orders
            setState {
                copy(
                    ongoingOrdersFiltered = null,
                    selectedFilter = null,
                    currentPage = 1
                )
            }
            loadOngoingLeads()
        } else {
            // Filter by specific status
            setState {
                copy(
                    selectedFilter = status,
                    currentPage = 1
                )
            }

            val apiStatus = mapDisplayToApiStatus(status)
            loadOngoingLeads(apiStatus)
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
            delay(300) // Debounce

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

        // Optionally load that specific status
        when (cardType) {
            "New Leads" -> loadOngoingLeads(LeadStatus.NEW.value)
            "Total Leads" -> loadOngoingLeads(null)
            "Approved" -> loadOngoingLeads(LeadStatus.APPROVED.value)
            "Not Repairable" -> loadOngoingLeads(LeadStatus.NOT_REPAIRABLE.value)
            "Completed" -> loadOngoingLeads(LeadStatus.COMPLETED.value)
            "Pending" -> loadOngoingLeads(LeadStatus.WORK_IN_PROGRESS.value)
        }
    }

    private fun handleOrderClick(orderId: String) {
        setAction { HomeAction.NavigateToOrderDetails(orderId) }
    }

    private fun createSummaryCards(stats: LeadStatsResponse.LeadStats): List<Triple<String, Int, Int>> {
        val counts = state.value.cardCounts
        return listOf(
            Triple("New Leads", R.drawable.audience, counts[LeadStatus.NEW.value] ?: stats.newLeads),
            Triple("Total Leads", R.drawable.total_leads, counts["TOTAL"] ?: stats.totalLeads),
            Triple("Approved", R.drawable.approved, counts[LeadStatus.APPROVED.value] ?: stats.approved),
            Triple("Not Repairable", R.drawable.not_repairable, counts[LeadStatus.NOT_REPAIRABLE.value] ?: stats.notRepairable),
            Triple("Completed", R.drawable.completed, counts[LeadStatus.COMPLETED.value] ?: stats.completed),
            Triple("Pending", R.drawable.pending, counts[LeadStatus.WORK_IN_PROGRESS.value] ?: stats.workInProgress)
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
            LeadStatus.NEW.value -> "New"
            LeadStatus.APPROVED.value -> "Approved"
            LeadStatus.NOT_REPAIRABLE.value -> "Not Repairable"
            LeadStatus.COMPLETED.value -> "Completed"
            LeadStatus.WORK_IN_PROGRESS.value -> "Work In Progress"
            LeadStatus.PICKUP_ALIGNED.value -> "Pickup Aligned"
            LeadStatus.PART_DELIVERED.value -> "Part Delivered"
            LeadStatus.PICKUP_DONE.value -> "Pickup Done"
            LeadStatus.INVOICE_GENERATED.value -> "Invoice Generated"
            LeadStatus.READY_FOR_DELIVERY.value -> "Ready for Delivery"
            else -> status.replace("_", " ")
                .split(" ")
                .joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
        }
    }

    private fun mapDisplayToApiStatus(display: String): String {
        return when (display) {
            "New" -> LeadStatus.NEW.value
            "Approved" -> LeadStatus.APPROVED.value
            "Not Repairable" -> LeadStatus.NOT_REPAIRABLE.value
            "Completed" -> LeadStatus.COMPLETED.value
            "Work In Progress" -> LeadStatus.WORK_IN_PROGRESS.value
            "Pickup Aligned" -> LeadStatus.PICKUP_ALIGNED.value
            "Part Delivered" -> LeadStatus.PART_DELIVERED.value
            "Pickup Done" -> LeadStatus.PICKUP_DONE.value
            "Invoice Generated" -> LeadStatus.INVOICE_GENERATED.value
            "Ready for Delivery" -> LeadStatus.READY_FOR_DELIVERY.value
            else -> display.uppercase().replace(" ", "_")
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
            // Try without milliseconds
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "TBD"
            } catch (e2: Exception) {
                // Try another common format
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    val date = inputFormat.parse(dateString)
                    date?.let { outputFormat.format(it) } ?: "TBD"
                } catch (e3: Exception) {
                    "TBD"
                }
            }
        }
    }
}