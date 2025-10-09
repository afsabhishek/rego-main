package com.rego.screens.main.home
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.main.home.data.LeadStatsResponse
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeInteractor(
    private val homeApi: HomeApi,
    private val userPreferences: UserPreferences
) {

    fun getLeadStats(): Flow<DataState<LeadStatsResponse.LeadStats>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // Get auth token from preferences
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
                            message = response.message ?: "Failed to load statistics"
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