package com.rego.screens.orderdetails

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.screens.orderdetails.data.OrderDetailsResponse
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderDetailsInteractor(
    private val api: OrderDetailsApi,
    private val userPreferences: UserPreferences
) {

    fun getLeadById(_id: String): Flow<DataState<OrderDetailsResponse>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            val authToken = userPreferences.getAuthToken()
            if (authToken.isNullOrEmpty()) {
                emit(
                    DataState.Error(
                        UIComponent.ErrorData(
                            title = "Authentication Error",
                            message = "Please login again",
                            buttonText = "Login"
                        )
                    )
                )
                return@flow
            }

            val response = api.getLeadById(authToken, _id)

            if (response.success && response.data != null) {
                emit(DataState.Data(response))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = "Lead not found"
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

    // ✅ Updated to accept List<String> for status
    fun getLeadsByStatus(
        status: List<String>? = null,
        partType: String? = null,
        page: Int = 1,
        limit: Int = 20,
        showLoading: Boolean = true
    ): Flow<DataState<LeadsResponse.LeadsData>> = flow {
        try {
            if (showLoading) {
                emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            }

            val authToken = userPreferences.getAuthToken()
            if (authToken.isNullOrEmpty()) {
                emit(
                    DataState.Error(
                        UIComponent.ErrorData(
                            title = "Authentication Error",
                            message = "Please login again",
                            buttonText = "Login"
                        )
                    )
                )
                return@flow
            }

            println("📥 OrderDetailsInteractor.getLeadsByStatus")
            println("   Status: $status")
            println("   PartType: $partType")
            println("   Page: $page")

            val response = api.getLeadsByStatus(
                authToken = authToken,
                status = status,
                partType = partType,
                page = page,
                limit = limit
            )

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = response.message ?: "Failed to fetch leads"
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
            if (showLoading) {
                emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
            }
        }
    }
}