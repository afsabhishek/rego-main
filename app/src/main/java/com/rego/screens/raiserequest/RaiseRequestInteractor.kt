// app/src/main/java/com/rego/screens/raiserequest/RaiseRequestInteractor.kt
package com.rego.screens.raiserequest

import android.net.Uri
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.raiserequest.data.*
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class RaiseRequestInteractor(
    private val api: RaiseRequestApi,
    private val userPreferences: UserPreferences
) {

    fun getVehicleMakes(): Flow<DataState<List<String>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getVehicleMakes()

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load vehicle makes"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getVehicleModels(make: String): Flow<DataState<List<VehicleModel>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getVehicleModels(make)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load vehicle models"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getVehicleVariants(
        make: String,
        model: String,
        fuelType: String
    ): Flow<DataState<List<VehicleVariant>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getVehicleVariants(make, model, fuelType)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load vehicle variants"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getWorkshopLocations(make: String): Flow<DataState<List<WorkshopLocation>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getWorkshopLocations(make)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load workshop locations"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getWorkshopDealers(
        make: String,
        location: String
    ): Flow<DataState<List<WorkshopDealer>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getWorkshopDealers(make, location)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load workshop dealers"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun getPartTypes(): Flow<DataState<List<PartTypeReference>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.getPartTypes()

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Failed to load part types"
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Connection Error",
                    message = "Unable to connect to server. Please check your internet connection.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun submitLead(
        request: CreateLeadRequest,
        imageUris: List<String>
    ): Flow<DataState<CreateLeadResponse.CreateLeadData>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // Validate request
            val validationError = validateLeadRequest(request)
            if (validationError != null) {
                emit(DataState.Error(
                    UIComponent.Snackbar(
                        message = validationError,
                        buttonText = "OK"
                    )
                ))
                return@flow
            }

            // Get auth token
            val authToken = userPreferences.getFirebaseIdToken()
            if (authToken.isNullOrEmpty()) {
                emit(DataState.Error(
                    UIComponent.ErrorData(
                        title = "Authentication Error",
                        message = "Please login again",
                        buttonText = "Login"
                    )
                ))
                return@flow
            }

            // Convert URIs to Files
            val imageFiles = imageUris.mapNotNull { uriString ->
                try {
                    Uri.parse(uriString)?.path?.let { File(it) }
                } catch (e: Exception) {
                    null
                }
            }

            val response = api.createLead(request, imageFiles, authToken)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message
                    )
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.Error(
                UIComponent.ErrorData(
                    title = "Submission Failed",
                    message = "Unable to submit request. Please try again.",
                    buttonText = "Retry"
                )
            ))
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    private fun validateLeadRequest(request: CreateLeadRequest): String? {
        return when {
            request.partType.isBlank() -> "Please select a part type"
            request.vehicleId.isBlank() -> "Please select a vehicle"
            request.registrationNumber.isBlank() -> "Registration number is required"
            !isValidRegistrationNumber(request.registrationNumber) -> "Invalid registration number format"
            request.makeYear < 1900 || request.makeYear > 2025 -> "Invalid make year"
            request.workshopId.isBlank() -> "Please select a workshop"
            request.advisorName.isBlank() -> "Advisor name is required"
            request.advisorContact.isBlank() -> "Advisor contact is required"
            !isValidPhoneNumber(request.advisorContact) -> "Invalid contact number. Must be 10 digits"
            request.policyType.isBlank() -> "Policy type is required"
            request.claimNumber.isBlank() -> "Claim number is required"
            !request.claimNumber.all { it.isDigit() } -> "Claim number must contain only numbers"
            else -> null
        }
    }

    private fun isValidRegistrationNumber(regNumber: String): Boolean {
        // Indian registration number format: XX00XX0000 or XX-00-XX-0000
        val pattern = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$".toRegex()
        val cleanedNumber = regNumber.replace("-", "").uppercase()
        return pattern.matches(cleanedNumber)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }
}