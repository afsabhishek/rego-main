package com.rego.screens.joinus

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.joinus.data.InsuranceCompaniesResponse
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.joinus.data.JoinUsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JoinUsInteractor(
    private val api: JoinUsApi
) {
    fun getInsuranceCompanies(
        page: Int = 1,
        limit: Int = 100
    ): Flow<DataState<InsuranceCompaniesResponse.InsuranceCompaniesData>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            val response = api.getInsuranceCompanies(page, limit)

            if (response.status == true && response.data != null) {
                emit(DataState.Data(data = response.data))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = response.message ?: "Failed to load insurance companies"
                        )
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        title = "Connection Error",
                        message = "Unable to connect to server. Please check your internet connection.",
                        buttonText = "Retry"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun submitJoinUsRequest(
        request: JoinUsRequest
    ): Flow<DataState<JoinUsResponse.JoinUsData>> = flow {
        try {
            // Validate request before sending
            val validationError = validateJoinUsRequest(request)
            if (validationError != null) {
                emit(
                    DataState.Error(
                        UIComponent.Snackbar(
                            message = validationError,
                            buttonText = "OK"
                        )
                    )
                )
                emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
                return@flow
            }

            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            val response = api.submitJoinUsRequest(request)

            if (response.status == true && response.data != null) {
                emit(DataState.Data(data = response.data))
                // Show success message
                if (response.data!!.message.isNotEmpty()) {
                    emit(
                        DataState.Error(
                            UIComponent.Snackbar(
                                message = response.data!!.message,
                                buttonText = "OK"
                            )
                        )
                    )
                }
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Registration Failed",
                            message = response.message ?: "Unable to complete registration"
                        )
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        title = "Connection Error",
                        message = "Unable to connect to server. Please check your internet connection.",
                        buttonText = "Retry"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    private fun validateJoinUsRequest(request: JoinUsRequest): String? {
        return when {
            request.name.isBlank() -> "Name is required"
            request.email.isBlank() -> "Email is required"
            !isValidEmail(request.email) -> "Invalid email format"
            request.phoneNumber.isBlank() -> "Phone number is required"
            !isValidPhoneNumber(request.phoneNumber) -> "Invalid phone number. Must be 10 digits"
            request.city.isBlank() -> "City is required"
            request.state.isBlank() -> "State is required"
            request.insuranceCompany.isBlank() -> "Insurance company is required"
            request.role.isBlank() -> "Role is required"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }
}