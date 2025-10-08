package com.rego.screens.main.profile

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import kotlinx.coroutines.launch

class ProfileViewModel(private val api: ProfileApi) :
    BaseViewModel<ProfileEvent, ProfileViewState, ProfileAction>() {

    override fun setInitialState() = ProfileViewState()

    override fun onTriggerEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Init -> init()
        }
    }

    private fun init() {
        viewModelScope.launch {
            setState { copy(progressBarState = ProgressBarState.Loading) }
            try {
                val response = api.getProfile()
                if (response.status == true && response.data != null) {
                    val profile = response.data!!
                    setState {
                        copy(
                            name = profile.name,
                            phone = profile.phone,
                            email = profile.email,
                            customerId = profile.customerId
                        )
                    }
                } else {
                    throw Exception(response.message ?: "Unknown Error")
                }
            } catch (e: Exception) {
                setError {
                    UIComponent.ErrorData(
                        title = "OOPS!",
                        message = e.message ?: "Unknown Error",
                        buttonText = "Retry"
                    )
                }
            } finally {
                setState { copy(progressBarState = ProgressBarState.Idle) }
            }
        }
    }
}