// app/src/main/java/com/rego/screens/raiserequest/RaiseRequestEvent.kt
package com.rego.screens.raiserequest

import com.rego.screens.base.ViewEvent
import com.rego.screens.raiserequest.data.*

/**
 * Events for the RaiseRequest feature
 */
sealed class RaiseRequestEvent : ViewEvent {

    /**
     * Initialize the screen and load initial data (part types, vehicle makes)
     */
    object Init : RaiseRequestEvent()

    /**
     * Generic field change event for form inputs
     * Handles all field updates including cascading dropdown selections
     * @param field The field name to update
     * @param value The new value for the field
     */
    data class FieldChanged(val field: String, val value: Any) : RaiseRequestEvent()

    /**
     * Submit the raise request form
     */
    object SubmitRequest : RaiseRequestEvent()

    /**
     * Retry loading initial data after an error
     */
    object RetryLoadingData : RaiseRequestEvent()

    /**
     * Select a part type for repair
     * @param partType The selected part type reference
     */
    data class SelectPartType(val partType: PartTypeReference) : RaiseRequestEvent()

    /**
     * Select a vehicle variant
     * @param variant The selected vehicle variant with ID
     */
    data class SelectVehicleVariant(val variant: VehicleVariant) : RaiseRequestEvent()

    /**
     * Select a workshop dealer
     * @param dealer The selected workshop dealer with ID
     */
    data class SelectWorkshopDealer(val dealer: WorkshopDealer) : RaiseRequestEvent()
}

/**
 * Field names used in FieldChanged events
 */
object RaiseRequestFields {
    const val SELECTED_VEHICLE_MAKE = "selectedVehicleMake"
    const val SELECTED_VEHICLE_MODEL = "selectedVehicleModel"
    const val SELECTED_FUEL_TYPE = "selectedFuelType"
    const val SELECTED_WORKSHOP_LOCATION = "selectedWorkshopLocation"
    const val REGISTRATION_NUMBER = "registrationNumber"
    const val MAKE_YEAR = "makeYear"
    const val IS_INVENTORY_PICKUP = "isInventoryPickup"
    const val ADVISOR_NAME = "advisorName"
    const val ADVISOR_CONTACT_NUMBER = "advisorContactNumber"
    const val SELECTED_POLICY_TYPE = "selectedPolicyType"
    const val CLAIM_NUMBER = "claimNumber"
    const val IMAGES = "images"
}