package com.rego.screens.main.home

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeInteractor(
    private val homeApi: HomeApi,
    private val userPreferences: UserPreferences
) {

    fun getLeadStats(): Flow<DataState<List<LeadStatsResponse.LeadStatItem>>> = flow {
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

            val response = homeApi.getLeadStats(authToken)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = "Failed to load statistics"
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

    fun getLeadsList(
        status: String? = null,
        partType: String? = null,
        registrationNumber: String? = null,
        claimNumber: String? = null,
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

            val response = homeApi.getLeads(
                authToken = authToken,
                status = status,
                partType = partType,
                registrationNumber = registrationNumber,
                claimNumber = claimNumber,
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
                            message = response.message ?: "Failed to load leads"
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

    fun searchLeads(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Flow<DataState<LeadsResponse.LeadsData>> = flow {
        try {
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

            val response = homeApi.searchLeads(
                authToken = authToken,
                query = query,
                page = page,
                limit = limit
            )

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Snackbar(
                            message = response.message ?: "No results found",
                            buttonText = "OK"
                        )
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Error(
                    UIComponent.Snackbar(
                        message = "Search failed",
                        buttonText = "Retry"
                    )
                )
            )
        }
    }
}