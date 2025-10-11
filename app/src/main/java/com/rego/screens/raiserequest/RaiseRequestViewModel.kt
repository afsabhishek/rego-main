package com.rego.screens.raiserequest

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.raiserequest.data.*
import kotlinx.coroutines.launch

class RaiseRequestViewModel(
    private val interactor: RaiseRequestInteractor
) : BaseViewModel<RaiseRequestEvent, RaiseRequestViewState, RaiseRequestAction>() {

    override fun setInitialState() = RaiseRequestViewState()

    override fun onTriggerEvent(event: RaiseRequestEvent) {
        when (event) {
            is RaiseRequestEvent.Init -> loadInitialData()
            is RaiseRequestEvent.FieldChanged -> handleFieldChange(event.field, event.value)
            is RaiseRequestEvent.SubmitRequest -> submitRequest()
            is RaiseRequestEvent.RetryLoadingData -> loadInitialData()
            is RaiseRequestEvent.SelectPartType -> selectPartType(event.partType)
            is RaiseRequestEvent.SelectVehicleVariant -> selectVehicleVariant(event.variant)
            is RaiseRequestEvent.SelectWorkshopDealer -> selectWorkshopDealer(event.dealer)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load part types
            interactor.getPartTypes().collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        setState {
                            copy(
                                partTypes = dataState.data ?: emptyList(),
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                    }
                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
                        setError { dataState.uiComponent }
                    }
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }
                    else -> {}
                }
            }

            // Load vehicle makes
            interactor.getVehicleMakes().collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        setState {
                            copy(
                                vehicleMakes = dataState.data ?: emptyList(),
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                    }
                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
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

    private fun handleFieldChange(field: String, value: Any) {
        when (field) {
            "selectedVehicleMake" -> handleVehicleMakeChange(value as String)
            "selectedVehicleModel" -> handleVehicleModelChange(value as String)
            "selectedFuelType" -> handleFuelTypeChange(value as String)
            "selectedWorkshopLocation" -> handleWorkshopLocationChange(value as String)
            "registrationNumber" -> setState { copy(registrationNumber = value as String) }
            "makeYear" -> setState { copy(makeYear = value as String) }
            "isInventoryPickup" -> setState { copy(isInventoryPickup = value as Boolean) }
            "advisorName" -> setState { copy(advisorName = value as String) }
            "advisorContactNumber" -> setState { copy(advisorContactNumber = value as String) }
            "selectedPolicyType" -> setState { copy(selectedPolicyType = value as String) }
            "claimNumber" -> setState { copy(claimNumber = value as String) }
            "images" -> setState { copy(images = value as List<String>) }
            else -> {}
        }
    }

    private fun handleVehicleMakeChange(make: String) {
        setState {
            copy(
                selectedVehicleMake = make,
                selectedVehicleModel = "",
                selectedFuelType = "",
                selectedVehicleVariant = null,
                vehicleModels = emptyList(),
                vehicleVariants = emptyList(),
                selectedWorkshopLocation = "",
                selectedWorkshopDealer = null,
                workshopLocations = emptyList(),
                workshopDealers = emptyList()
            )
        }

        if (make.isNotBlank()) {
            // Load models for selected make
            viewModelScope.launch {
                setState { copy(isLoadingModels = true) }
                interactor.getVehicleModels(make).collect { dataState ->
                    when (dataState) {
                        is DataState.Data -> {
                            setState {
                                copy(
                                    vehicleModels = dataState.data ?: emptyList(),
                                    isLoadingModels = false
                                )
                            }
                        }
                        is DataState.Error -> {
                            setState { copy(isLoadingModels = false) }
                            setError { dataState.uiComponent }
                        }
                        else -> {}
                    }
                }
            }

            // Load workshop locations for selected make
            viewModelScope.launch {
                setState { copy(isLoadingLocations = true) }
                interactor.getWorkshopLocations(make).collect { dataState ->
                    when (dataState) {
                        is DataState.Data -> {
                            setState {
                                copy(
                                    workshopLocations = dataState.data ?: emptyList(),
                                    isLoadingLocations = false
                                )
                            }
                        }
                        is DataState.Error -> {
                            setState { copy(isLoadingLocations = false) }
                            setError { dataState.uiComponent }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun handleVehicleModelChange(model: String) {
        setState {
            copy(
                selectedVehicleModel = model,
                selectedFuelType = "",
                selectedVehicleVariant = null,
                vehicleVariants = emptyList()
            )
        }
    }

    private fun handleFuelTypeChange(fuelType: String) {
        setState {
            copy(
                selectedFuelType = fuelType,
                selectedVehicleVariant = null,
                vehicleVariants = emptyList()
            )
        }

        val make = state.value.selectedVehicleMake
        val model = state.value.selectedVehicleModel

        if (make.isNotBlank() && model.isNotBlank() && fuelType.isNotBlank()) {
            // Convert display name to API value
            val fuelTypeValue = FuelType.values()
                .find { it.displayName == fuelType }?.value ?: return

            // Load variants for selected combination
            viewModelScope.launch {
                setState { copy(isLoadingVariants = true) }
                interactor.getVehicleVariants(make, model, fuelTypeValue).collect { dataState ->
                    when (dataState) {
                        is DataState.Data -> {
                            setState {
                                copy(
                                    vehicleVariants = dataState.data ?: emptyList(),
                                    isLoadingVariants = false
                                )
                            }
                        }
                        is DataState.Error -> {
                            setState { copy(isLoadingVariants = false) }
                            setError { dataState.uiComponent }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun handleWorkshopLocationChange(location: String) {
        setState {
            copy(
                selectedWorkshopLocation = location,
                selectedWorkshopDealer = null,
                workshopDealers = emptyList()
            )
        }

        val make = state.value.selectedVehicleMake

        if (make.isNotBlank() && location.isNotBlank()) {
            // Load dealers for selected make and location
            viewModelScope.launch {
                setState { copy(isLoadingDealers = true) }
                interactor.getWorkshopDealers(make, location).collect { dataState ->
                    when (dataState) {
                        is DataState.Data -> {
                            setState {
                                copy(
                                    workshopDealers = dataState.data ?: emptyList(),
                                    isLoadingDealers = false
                                )
                            }
                        }
                        is DataState.Error -> {
                            setState { copy(isLoadingDealers = false) }
                            setError { dataState.uiComponent }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun selectPartType(partType: PartTypeReference) {
        setState { copy(selectedPartType = partType) }
    }

    private fun selectVehicleVariant(variant: VehicleVariant) {
        setState { copy(selectedVehicleVariant = variant) }
    }

    private fun selectWorkshopDealer(dealer: WorkshopDealer) {
        setState { copy(selectedWorkshopDealer = dealer) }
    }

    private fun submitRequest() {
        val currentState = state.value

        if (!currentState.isFormValid) {
            setError {
                com.rego.screens.base.UIComponent.Snackbar(
                    message = "Please fill all required fields",
                    buttonText = "OK"
                )
            }
            return
        }

        // Convert policy type display name to value
        val policyTypeValue = PolicyType.fromDisplayName(currentState.selectedPolicyType)?.value
            ?: currentState.selectedPolicyType

        val request = CreateLeadRequest(
            partType = currentState.selectedPartTypeSlug,
            vehicleId = currentState.selectedVehicleId,
            registrationNumber = currentState.registrationNumber.uppercase(),
            makeYear = currentState.makeYear.toIntOrNull() ?: 2024,
            inventoryPickUp = currentState.isInventoryPickup,
            workshopId = currentState.selectedWorkshopId,
            advisorName = currentState.advisorName.trim(),
            advisorContact = currentState.advisorContactNumber.trim(),
            policyType = policyTypeValue,
            claimNumber = currentState.claimNumber.trim()
        )

        viewModelScope.launch {
            interactor.submitLead(request, currentState.images).collect { dataState ->
                when (dataState) {
                    is DataState.Data -> {
                        setState {
                            copy(
                                submitResult = dataState.data,
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                        // Navigate to success screen
                        setAction { RaiseRequestAction.NavigateToSuccess }
                    }
                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
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