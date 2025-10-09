package com.rego.screens.main.profile

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.mobileverification.data.User
import com.rego.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileInteractor(
    private val api: ProfileApi,
    private val userPreferences: UserPreferences
) {
    fun getUserProfile(): Flow<DataState<User>> = flow {
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

            val response = api.getUserProfile(authToken)

            if (response.success && response.data != null) {
                emit(DataState.Data(response.data.user))
            } else {
                emit(
                    DataState.Error(
                        UIComponent.Dialog(
                            title = "Error",
                            message = response.message ?: "Failed to load profile"
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