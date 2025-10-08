package com.rego.screens.mobileverification

import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.mobileverification.data.OtpResponse
import com.rego.screens.mobileverification.data.VerifyOtpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MobileVerificationInteractor(
    private val api: MobileVerificationApi
) {

    fun requestOtp(phoneNumber: String): Flow<DataState<OtpResponse.Data>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.requestOtp(phoneNumber)
            if (response.status == true && response.data != null) {
                emit(DataState.Data(data = response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Request failed"
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

    fun verifyOtp(
        phoneNumber: String,
        otp: String
    ): Flow<DataState<VerifyOtpResponse.LoginData>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.verifyOtp(phoneNumber, otp)
            if (response.status == true && response.data != null) {
                emit(DataState.Data(data = response.data))
            } else {
                emit(DataState.Error(
                    UIComponent.Snackbar(
                        message = response.message ?: "Verification failed",
                        buttonText = "OK"
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

    fun resendOtp(
        phoneNumber: String
    ): Flow<DataState<OtpResponse.Data>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))
            val response = api.resendOtp(phoneNumber)
            if (response.status == true && response.data != null) {
                emit(DataState.Data(data = response.data))
                if (response.data!!.message.isNotEmpty()) {
                    emit(DataState.Error(
                        UIComponent.Snackbar(
                            message = response.data!!.message,
                            buttonText = "OK"
                        )
                    ))
                }
            } else {
                emit(DataState.Error(
                    UIComponent.Dialog(
                        title = "Error",
                        message = response.message ?: "Resend failed"
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
}