package com.rego.screens.orderdetails

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.main.home.data.LeadsResponse
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val interactor: OrderDetailsInteractor
) : BaseViewModel<OrderDetailsEvent, OrderDetailsViewState, OrderDetailsAction>() {

    override fun setInitialState() = OrderDetailsViewState()

    override fun onTriggerEvent(event: OrderDetailsEvent) {
        when (event) {
            is OrderDetailsEvent.Init -> {
                // Initial state, waiting for specific actions
            }

            is OrderDetailsEvent.LoadLeadDetails -> {
                loadLeadDetails(event.leadId)
            }

            is OrderDetailsEvent.LoadLeadsByStatus -> {
                // ✅ Updated to accept List<String> for status
                loadLeadsByStatus(event.status, partType = null)
            }

            is OrderDetailsEvent.RetryLoadDetails -> {
                state.value.selectedLeadId?.let { leadId ->
                    loadLeadDetails(leadId)
                }
            }

            is OrderDetailsEvent.LoadMoreLeads -> {
                loadMoreLeads()
            }
        }
    }

    private fun loadLeadDetails(leadId: String) {
        viewModelScope.launch {
            setState { copy(selectedLeadId = leadId) }

            interactor.getLeadById(leadId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }

                    is DataState.Data -> {
                        setState {
                            copy(
                                selectedLead = dataState.data,
                                progressBarState = ProgressBarState.Idle,
                                error = null
                            )
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                progressBarState = ProgressBarState.Idle,
                                error = "Failed to load lead details"
                            )
                        }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    // ✅ Updated to accept List<String> for status
    private fun loadLeadsByStatus(status: List<String>? = null, partType: String? = null) {
        viewModelScope.launch {
            println("📥 loadLeadsByStatus called")
            println("   Status: $status")
            println("   PartType: $partType")

            setState { copy(currentStatus = status, currentPartType = partType) }

            interactor.getLeadsByStatus(status, partType).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState {
                            copy(
                                progressBarState = dataState.progressBarState
                            )
                        }
                    }

                    is DataState.Data -> {
                        val newLeads = dataState.data?.leads ?: emptyList()
                        println("✅ Leads received: ${newLeads.size} items")

                        setState {
                            copy(
                                leads = newLeads,
                                pagination = dataState.data?.pagination,
                                hasMorePages = dataState.data?.pagination?.hasNextPage ?: false,
                                progressBarState = ProgressBarState.Idle,
                                isLoadingMore = false,
                                error = null
                            )
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                progressBarState = ProgressBarState.Idle,
                                isLoadingMore = false,
                                error = "Failed to load leads"
                            )
                        }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    // ✅ New public function to load with BOTH status and part type
    fun loadLeadsByStatusWithPartType(status: List<String>? = null, partType: String?) {
        println("📥 loadLeadsByStatusWithPartType called")
        println("   Status: $status")
        println("   PartType: $partType")

        loadLeadsByStatus(status = status, partType = partType)
    }

    private fun loadMoreLeads() {
        if (state.value.isLoadingMore || !state.value.hasMorePages) return

        val nextPage = state.value.currentPage + 1

        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }

            // ✅ Load next page with current filters
            interactor.getLeadsByStatus(
                status = state.value.currentStatus,
                partType = state.value.currentPartType,
                page = nextPage
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        dataState.data?.let { leadsData ->
                            val newLeads = leadsData.leads

                            setState {
                                copy(
                                    leads = state.value.leads + newLeads,
                                    pagination = leadsData.pagination,
                                    currentPage = leadsData.pagination.currentPage,
                                    hasMorePages = leadsData.pagination.hasNextPage,
                                    isLoadingMore = false
                                )
                            }
                        }
                    }

                    is DataState.Error -> {
                        setState { copy(isLoadingMore = false) }
                    }

                    else -> {}
                }
            }
        }
    }
}