package com.rego.screens.mobileverification

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MobileVerificationViewModel(
    private val interactor: MobileVerificationInteractor
) : BaseViewModel<MobileVerificationEvent, MobileVerificationViewState, MobileVerificationAction>() {

    override fun setInitialState() = MobileVerificationViewState()

    override fun onTriggerEvent(event: MobileVerificationEvent) {
        when (event) {
            is MobileVerificationEvent.Init -> {
                setState { copy(progressBarState = ProgressBarState.Idle) }
            }

            is MobileVerificationEvent.MobileNumberChanged -> {
                setState {
                    copy(
                        mobileNumber = event.value,
                        errorMessage = null
                    )
                }
            }

            is MobileVerificationEvent.GetOtp -> {
                requestOtp()
            }

            is MobileVerificationEvent.OtpChanged -> {
                setState {
                    copy(
                        otp = event.value,
                        errorMessage = null // Clear error on OTP change
                    )
                }
            }

            is MobileVerificationEvent.VerifyOtp -> {
                verifyOtp()
            }

            is MobileVerificationEvent.ResendOtp -> {
                resendOtp()
            }
        }
    }

    private fun requestOtp() {
        viewModelScope.launch {
            interactor.requestOtp(state.value.mobileNumber).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { otpData ->
                            if (otpData.otpSent) {
                                setState {
                                    copy(
                                        isOtpRequested = true,
                                        backendMessage = otpData.message
                                    )
                                }
                                startResendTimer()

                                // Show success message from backend
                                setError {
                                    UIComponent.Snackbar(
                                        message = otpData.message,
                                        buttonText = "OK"
                                    )
                                }
                            }
                        }
                    }

                    is DataState.Error -> {
                        // Backend error message will be shown by DefaultScreenUI
                        setError { dataState.uiComponent }
                    }

                    is DataState.NetworkStatus -> {
                        // Handle network status if needed
                    }

                    is DataState.Error -> {
                        // Backend error message will be shown by DefaultScreenUI
                        setError { dataState.uiComponent }
                    }
                }
            }
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            interactor.verifyOtp(
                state.value.mobileNumber,
                state.value.otp
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { verifyData ->
                            setState {
                                copy(
                                    isOtpVerified = true,
                                    authToken = verifyData.authentication.authToken,
                                    refreshToken = verifyData.authentication.refreshToken,
                                    tokenExpiresIn = verifyData.authentication.expiresIn,
                                    tokenType = verifyData.authentication.tokenType,
                                    userId = verifyData.user.id,
                                    userName = verifyData.user.name,
                                    userEmail = verifyData.user.email,
                                    userCity = verifyData.user.city,
                                    userState = verifyData.user.state,
                                    userInsuranceCompany = verifyData.user.insuranceCompany,
                                    userRole = verifyData.user.role,
                                    backendMessage = verifyData.message
                                )
                            }

                            delay(500)
                            setAction { MobileVerificationAction.NavigateToHome }
                        }
                    }

                    is DataState.Error -> {
                        // Backend error message will be shown
                        setError { dataState.uiComponent }
                    }

                    is DataState.NetworkStatus -> {
                        // Handle network status if needed
                    }
                }
            }
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            setState { copy(otp = "") } // Clear previous OTP

            interactor.resendOtp(state.value.mobileNumber).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { otpData ->
                            if (otpData.otpSent) {
                                setState {
                                    copy(
                                        resendCount = resendCount + 1,
                                        backendMessage = otpData.message
                                    )
                                }
                                startResendTimer()
                            }
                        }
                    }

                    is DataState.Error -> {
                        // Backend error message will be shown
                        setError { dataState.uiComponent }
                    }

                    is DataState.NetworkStatus -> {
                        // Handle network status if needed
                    }
                }
            }
        }
    }

    private fun startResendTimer() {
        viewModelScope.launch {
            setState { copy(resendTimer = 30, isResendEnabled = false) }
            while (state.value.resendTimer > 0) {
                delay(1000)
                setState { copy(resendTimer = state.value.resendTimer - 1) }
            }
            setState { copy(isResendEnabled = true) }
        }
    }
}
