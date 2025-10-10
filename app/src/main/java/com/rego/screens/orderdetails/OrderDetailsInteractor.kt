package com.rego.screens.orderdetails

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderDetailsInteractor(
    private val api: OrderDetailsApi,
    private val userPreferences: UserPreferences
) {

    fun getLeadById(leadId: String): Flow<DataState<LeadsResponse.LeadsData.Lead>> = flow {
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

            val response = api.getLeadById(authToken, leadId)

            if (response.success && response.data != null && response.data.leads.isNotEmpty()) {
                emit(DataState.Data(response.data.leads.first()))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = response.message ?: "Lead not found"
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

    fun getLeadsByStatus(
        status: String?,
        page: Int = 1,
        limit: Int = 20
    ): Flow<DataState<LeadsResponse.LeadsData>> = flow {
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

            val response = api.getLeadsByStatus(authToken, status, page, limit)

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
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}