// app/src/main/java/com/rego/screens/raiserequest/RaiseRequestViewState.kt
package com.rego.screens.raiserequest

import androidx.compose.runtime.Immutable
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.ViewState
import com.rego.screens.raiserequest.data.*

@Immutable
data class RaiseRequestViewState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,

    // Part Types
    val partTypes: List<PartTypeReference> = emptyList(),
    val selectedPartType: PartTypeReference? = null,

    // Vehicle Selection
    val vehicleMakes: List<VehicleMake> = emptyList(),
    val selectedVehicleMake: String = "",
    val vehicleModels: List<VehicleModel> = emptyList(),
    val selectedVehicleModel: String = "",
    val fuelTypes: List<String> = FuelType.getAllDisplayNames(),
    val selectedFuelType: String = "",
    val vehicleVariants: List<VehicleVariant> = emptyList(),
    val selectedVehicleVariant: VehicleVariant? = null,

    // Workshop Selection
    val workshopLocations: List<WorkshopLocation> = emptyList(),
    val selectedWorkshopLocation: String = "",
    val workshopDealers: List<WorkshopDealer> = emptyList(),
    val selectedWorkshopDealer: WorkshopDealer? = null,

    // Form Fields
    val registrationNumber: String = "",
    val makeYear: String = "",
    val isInventoryPickup: Boolean = false,
    val advisorName: String = "",
    val advisorContactNumber: String = "",
    val policyTypes: List<String> = PolicyType.getAllDisplayNames(),
    val selectedPolicyType: String = "",
    val claimNumber: String = "",

    // Images
    val images: List<String> = emptyList(), // URI strings

    // Loading states for cascading dropdowns
    val isLoadingModels: Boolean = false,
    val isLoadingVariants: Boolean = false,
    val isLoadingLocations: Boolean = false,
    val isLoadingDealers: Boolean = false,

    // Submission
    val submitResult: CreateLeadResponse.CreateLeadData? = null,
    val error: String? = null
) : ViewState {

    // Computed properties for form validation
    val isVehicleSelectionComplete: Boolean
        get() = selectedVehicleVariant != null

    val isWorkshopSelectionComplete: Boolean
        get() = selectedWorkshopDealer != null

    val isFormValid: Boolean
        get() = selectedPartType != null &&
                isVehicleSelectionComplete &&
                isWorkshopSelectionComplete &&
                registrationNumber.isNotBlank() &&
                makeYear.isNotBlank() &&
                advisorName.isNotBlank() &&
                advisorContactNumber.length == 10 &&
                selectedPolicyType.isNotBlank() &&
                claimNumber.isNotBlank()

    // Get the selected vehicle ID for API
    val selectedVehicleId: String
        get() = selectedVehicleVariant?.id ?: ""

    // Get the selected workshop ID for API
    val selectedWorkshopId: String
        get() = selectedWorkshopDealer?.id ?: ""

    // Get the part type slug for API
    val selectedPartTypeSlug: String
        get() = selectedPartType?.slug ?: ""
}