package com.rego.screens.main.profile

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val interactor: ProfileInteractor
) : BaseViewModel<ProfileEvent, ProfileViewState, ProfileAction>() {

    override fun setInitialState() = ProfileViewState()

    override fun onTriggerEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Init -> loadUserProfile()
        }
    }

    fun loadUserProfile() {
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
                        }
                    }

                    is DataState.Error -> {
                        setState { copy(progressBarState = ProgressBarState.Idle) }
                        setError { dataState.uiComponent }
                    }

                    else -> {}
                }
            }
        }
    }
}