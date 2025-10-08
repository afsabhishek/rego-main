package com.rego.screens.notifications

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationInteractor(private val api: NotificationApi) {
    fun getNotifications(): Flow<DataState<List<NotificationItem>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val notifications = api.getNotifications()
            emit(DataState.Data(notifications))
        } catch (e: Exception) {
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        title = "Failed",
                        message = e.message ?: "Unknown Error",
                        buttonText = "Retry"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
