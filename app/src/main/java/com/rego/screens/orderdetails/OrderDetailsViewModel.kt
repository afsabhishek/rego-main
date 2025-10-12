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
                loadLeadsByStatus(event.status, event.page)
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

    private fun loadLeadsByStatus(status: String?, page: Int = 1, append: Boolean = false) {
        viewModelScope.launch {
            if (!append) {
                setState { copy(currentStatus = status, currentPage = page) }
            }

            interactor.getLeadsByStatus(status, page).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState {
                            copy(
                                progressBarState = dataState.progressBarState,
                                isLoadingMore = append
                            )
                        }
                    }

                    is DataState.Data -> {
                        val newLeads = dataState.data?.leads ?: emptyList()

                        setState {
                            copy(
                                leads = if (append) leads + newLeads else newLeads,
                                pagination = dataState.data?.pagination,
                                currentPage = dataState.data?.pagination?.currentPage ?: page,
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

        val nextPage = state.value.currentPage + 1
        loadLeadsByStatus(
            status = state.value.currentStatus,
            page = nextPage,
            append = true
        )
    }
}