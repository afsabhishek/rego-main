package com.rego.screens.mobileverification

import androidx.lifecycle.viewModelScope
import com.rego.auth.FirebaseAuthManager
import com.rego.screens.auth.AuthInteractor
import com.rego.screens.auth.FirebaseAuthResult
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.UIComponent.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.rego.util.UserPreferences

class MobileVerificationViewModel(
    private val interactor: MobileVerificationInteractor,
    private val userPreferences: UserPreferences,
    private val authInteractor: AuthInteractor,
    private val firebaseAuthManager: FirebaseAuthManager
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
                        errorMessage = null
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
                        setState { copy(isOtpLoading = true, errorMessage = null) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { otpData ->
                            if (otpData.otpSent) {
                                setState {
                                    copy(
                                        isOtpLoading = false,
                                        isOtpRequested = true,
                                        backendMessage = otpData.message
                                    )
                                }
                                startResendTimer()

                                setError {
                                    Snackbar(
                                        message = otpData.message,
                                        buttonText = "OK"
                                    )
                                }
                            }
                        }
                    }

                    is DataState.Error -> {
                        setState { copy(isOtpLoading = false) }
                        setError { dataState.uiComponent }
                    }

                    is DataState.NetworkStatus -> {
                        // Handle network status if needed
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
                        setState { copy(isOtpLoading = true, errorMessage = null) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { verifyData ->

                            userPreferences.clearAll()
                            // Save backend tokens
                            userPreferences.saveAuthToken(
                                verifyData.authentication.authToken,
                                verifyData.authentication.expiresIn
                            )
                            verifyData.user.role = "CR"
                            verifyData.authentication.refreshToken?.let {
                                userPreferences.saveRefreshToken(it)
                            }

                            // Save user info
                            userPreferences.saveUserInfo(
                                userId = verifyData.user.id,
                                userName = verifyData.user.name,
                                email = verifyData.user.email,
                                phone = verifyData.user.phoneNumber
                            )

                            // Check if we need to authenticate with Firebase
                            // Assuming backend returns Firebase custom token in response
                            val firebaseCustomToken = verifyData.firebaseCustomToken

                            if (firebaseCustomToken != null) {
                                authenticateWithFirebase(firebaseCustomToken, verifyData)
                            } else {
                                // No Firebase token, proceed without Firebase auth
                                completeAuthentication(verifyData, null)
                            }
                        }
                    }

                    is DataState.Error -> {
                        setError { dataState.uiComponent }
                    }

                    is DataState.NetworkStatus -> {
                        // Handle network status if needed
                    }
                }
            }
        }
    }

    private fun authenticateWithFirebase(
        customToken: String,
        verifyData: com.rego.screens.mobileverification.data.VerifyOtpResponse.LoginData
    ) {
        viewModelScope.launch {
            authInteractor.authenticateWithFirebase(customToken).collect { result ->
                when (result) {
                    is FirebaseAuthResult.Success -> {
                        completeAuthentication(verifyData, result.idToken)
                    }
                    is FirebaseAuthResult.Error -> {
                        // Firebase auth failed, but we can still proceed with backend token
                        setError {
                            Snackbar(
                                message = "Firebase authentication failed: ${result.message}",
                                buttonText = "OK"
                            )
                        }
                        completeAuthentication(verifyData, null)
                    }
                }
            }
        }
    }

    private fun completeAuthentication(
        verifyData: com.rego.screens.mobileverification.data.VerifyOtpResponse.LoginData,
        firebaseIdToken: String?
    ) {
        viewModelScope.launch {
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
                    firebaseIdToken = firebaseIdToken,
                    backendMessage = verifyData.message
                )
            }

            userPreferences.saveUserRole(verifyData.user.role)  // ✅ ADD THIS
            userPreferences.saveUserInfoWithRole(
                userId = verifyData.user.id,
                userName = verifyData.user.name,
                email = verifyData.user.email,
                phone = verifyData.user.phoneNumber,
                role = verifyData.user.role  // ✅ USE THIS
            )

            delay(500)
            setAction { MobileVerificationAction.NavigateToHome }
        }
    }

    private fun resendOtp() {
        viewModelScope.launch {
            setState { copy(otp = "") }

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