package com.rego.screens.main.profile

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.util.UserPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val interactor: ProfileInteractor,
    private val userPreferences: UserPreferences
) : BaseViewModel<ProfileEvent, ProfileViewState, ProfileAction>() {

    override fun setInitialState() = ProfileViewState()

    override fun onTriggerEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Init -> loadUserProfile()
            is ProfileEvent.Refresh -> loadUserProfile()
            is ProfileEvent.Logout -> logout()
        }
    }

    init {
        // Auto-load profile on ViewModel creation
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            interactor.getUserProfile().collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState { copy(progressBarState = dataState.progressBarState) }
                    }

                    is DataState.Data -> {
                        dataState.data?.let { profile ->
                            setState {
                                copy(
                                    name = profile.name,
                                    phone = profile.phoneNumber,
                                    email = profile.email,
                                    customerId = profile.id,
                                    city = profile.city,
                                    state = profile.state,
                                    insuranceCompany = profile.insuranceCompany,
                                    role = profile.role,
                                    progressBarState = ProgressBarState.Idle
                                )
                            }

                            // Also save to preferences for offline access
                            userPreferences.saveUserInfo(
                                userId = profile.id,
                                userName = profile.name,
                                email = profile.email,
                                phone = profile.phoneNumber
                            )
                        }
                    }

                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }

                        // Try to load from cached data if API fails
                        loadCachedProfile()

                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun loadCachedProfile() {
        viewModelScope.launch {
            val cachedName = userPreferences.getUserName()
            val cachedId = userPreferences.getUserId()

            if (!cachedName.isNullOrBlank()) {
                setState {
                    copy(
                        name = cachedName,
                        customerId = cachedId,
                        progressBarState = ProgressBarState.Idle
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setState { copy(progressBarState = ProgressBarState.Loading) }

            try {
                // Clear all user data
                userPreferences.clearAll()

                // Navigate to login
                setAction { ProfileAction.NavigateToLogin }
            } catch (e: Exception) {
                setState { copy(progressBarState = ProgressBarState.Idle) }
                setAction {
                    ProfileAction.ShowError("Failed to logout. Please try again.")
                }
            }
        }
    }
}