package com.rego.screens.raiserequest

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import kotlinx.coroutines.launch

class RaiseRequestViewModel(private val interactor: RaiseRequestInteractor) :
    BaseViewModel<RaiseRequestEvent, RaiseRequestViewState, RaiseRequestAction>() {

    override fun setInitialState() = RaiseRequestViewState()

    override fun onTriggerEvent(event: RaiseRequestEvent) {
        when (event) {
            is RaiseRequestEvent.Init -> loadFormOptions()
            is RaiseRequestEvent.FieldChanged -> setStateForField(event.field, event.value)
            is RaiseRequestEvent.SubmitRequest -> submitRequest(event.data)
        }
    }

    private fun loadFormOptions() {
        viewModelScope.launch {
            interactor.getFormOptions().collect { dataState ->
                when (dataState) {
                    is DataState.Data -> setState {
                        copy(
                            formOptions = dataState.data,
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Error -> setState {
                        copy(
                            error = dataState.uiComponent.toString(),
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Loading -> setState { copy(progressBarState = dataState.progressBarState) }
                    else -> {}
                }
            }
        }
    }

    private fun setStateForField(field: String, value: Any) {
        setState {
            when (field) {
                "selectedCarMake" -> copy(selectedCarMake = value as String)
                "selectedCarModel" -> copy(selectedCarModel = value as String)
                "selectedFuelType" -> copy(selectedFuelType = value as String)
                "selectedCarVariant" -> copy(selectedCarVariant = value as String)
                "selectedDealerLocation" -> copy(selectedDealerLocation = value as String)
                "selectedPolicyType" -> copy(selectedPolicyType = value as String)
                "dealerName" -> copy(dealerName = value as String)
                "advisorName" -> copy(advisorName = value as String)
                "advisorContactNumber" -> copy(advisorContactNumber = value as String)
                "claimNumber" -> copy(claimNumber = value as String)
                "carRegNumber" -> copy(carRegNumber = value as String)
                "makeYear" -> copy(makeYear = value as String)
                "isInventoryPickup" -> copy(isInventoryPickup = value as Boolean)
                "selectedPartType" -> copy(selectedPartType = value as String)
                "images" -> copy(images = value as List<String>)
                else -> this
            }
        }
    }

    private fun submitRequest(data: RaiseRequestFormData) {
        viewModelScope.launch {
            interactor.submitRequest(data).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> setState {
                        copy(
                            submitResult = dataState.data,
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Error -> setState {
                        copy(
                            error = dataState.uiComponent.toString(),
                            progressBarState = ProgressBarState.Idle
                        )
                    }

                    is DataState.Loading -> setState { copy(progressBarState = dataState.progressBarState) }
                    else -> {}
                }
            }
        }
    }
}
