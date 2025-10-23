package com.rego.screens.orderdetails

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
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
                loadLeadDetails(event._id)
            }

            is OrderDetailsEvent.LoadLeadsByStatus -> {
                // ✅ Updated to accept List<String> for status
                loadLeadsByStatus(event.status, partType = null)
            }

            is OrderDetailsEvent.RetryLoadDetails -> {
                state.value.selectedId?.let { _id ->
                    loadLeadDetails(_id)
                }
            }
            is OrderDetailsEvent.AcceptLead -> acceptLead(event.leadId)
            is OrderDetailsEvent.RejectLead -> rejectLead(event.leadId)
        }
    }

    private fun loadLeadDetails(_id: String) {
        viewModelScope.launch {
            setState { copy(selectedId = _id) }

            interactor.getLeadById(_id).collect { dataState ->
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
                        setState {
                            copy(
                                leads = newLeads,
                                progressBarState = ProgressBarState.Idle,
                                error = null
                            )
                        }
                    }

                    is DataState.Error -> {
                        setState {
                            copy(
                                progressBarState = ProgressBarState.Idle,
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

    private fun acceptLead(leadId: String) {
        viewModelScope.launch {
            interactor.acceptLead(leadId).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        setAction { OrderDetailsAction.LeadAccepted(leadId) }
                    }
                    is DataState.Error -> {
                        setError { dataState.uiComponent }
                    }
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun rejectLead(leadId: String) {
        viewModelScope.launch {
            interactor.rejectLead(leadId).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        setAction { OrderDetailsAction.LeadRejected(leadId) }
                    }
                    is DataState.Error -> {
                        setError { dataState.uiComponent }
                    }
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }
                    else -> {}
                }
            }
        }

    }
}