package com.rego.screens.main.home

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeInteractor(private val homeApi: HomeApi) {
    fun getData(): Flow<DataState<String>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val apiResponse = homeApi.getHomeData()
            if (apiResponse.status != true || apiResponse.data == null) {
                throw Exception(
                    apiResponse.message ?: "Unknown Error"
                )
            }
            val result = apiResponse.data
            emit(DataState.Data(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Error(
                    UIComponent.ErrorData(
                        "OOPS!",
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