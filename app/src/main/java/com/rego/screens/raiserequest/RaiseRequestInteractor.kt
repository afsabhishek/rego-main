package com.rego.screens.raiserequest

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RaiseRequestInteractor(private val api: RaiseRequestApi) {
    fun getFormOptions(): Flow<DataState<RaiseRequestFormOptions>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val options = api.getFormOptions()
            emit(DataState.Data(options))
        } catch (e: Exception) {
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        "Failed",
                        e.message ?: "Unknown Error",
                        "Retry"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }

    fun submitRequest(data: RaiseRequestFormData): Flow<DataState<Boolean>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val result = api.submitRequest(data)
            emit(DataState.Data(result))
        } catch (e: Exception) {
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        "Failed",
                        e.message ?: "Unknown Error",
                        "Retry"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}
