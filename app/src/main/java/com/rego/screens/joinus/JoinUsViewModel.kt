package com.rego.screens.joinus

import androidx.lifecycle.viewModelScope
import com.rego.screens.base.BaseViewModel
import com.rego.screens.base.DataState
import com.rego.screens.base.ProgressBarState
import com.rego.screens.base.UIComponent
import com.rego.screens.joinus.data.InsuranceCompany
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.mobileverification.MobileVerificationAction
import com.rego.screens.mobileverification.MobileVerificationEvent
import com.rego.screens.mobileverification.MobileVerificationViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class JoinUsViewModel(
    private val interactor: JoinUsInteractor
) : BaseViewModel<JoinUsEvent, JoinUsViewState, JoinUsAction>() {

    override fun setInitialState() = JoinUsViewState()

    override fun onTriggerEvent(event: JoinUsEvent) {
        when (event) {
            is JoinUsEvent.LoadInsuranceCompanies -> loadInsuranceCompanies()
            is JoinUsEvent.SelectInsuranceCompany -> selectInsuranceCompany(event.company)
            is JoinUsEvent.SubmitRegistration -> submitRegistration(
                name = event.name,
                email = event.email,
                phoneNumber = event.phoneNumber,
                city = event.city,
                state = event.state,
                company = event.company,
                role = event.role
            )
            is JoinUsEvent.RetryLoadingCompanies -> loadInsuranceCompanies()
        }
    }

    init {
        setEvent(JoinUsEvent.LoadInsuranceCompanies)
    }

    private fun loadInsuranceCompanies() {
        interactor.getInsuranceCompanies()
            .onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState {
                            copy(progressBarState = dataState.progressBarState)
                        }
                    }

                    is DataState.Data -> {
                        setState {
                            copy(
                                insuranceCompanies = dataState.data?.insuranceCompanies ?: emptyList(),
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                    }

                    is DataState.Error -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog -> {
                                setAction { JoinUsAction.ShowDialog(dataState.uiComponent) }
                            }
                            is UIComponent.ErrorData -> {
                                setAction { JoinUsAction.ShowErrorScreen(dataState.uiComponent) }
                            }
                            is UIComponent.Snackbar -> {
                                setAction { JoinUsAction.ShowSnackbar(dataState.uiComponent) }
                            }
                            else -> {}
                        }
                    }

                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    private fun selectInsuranceCompany(company: InsuranceCompany) {
        setState { copy(selectedCompany = company) }
    }

    private fun submitRegistration(
        name: String,
        email: String,
        phoneNumber: String,
        city: String,
        state: String,
        company: String,
        role: String
    ) {

        if (company.isNullOrBlank()) {
            setAction {
                JoinUsAction.ShowSnackbar(
                    UIComponent.Snackbar(
                        message = "Please select an insurance company",
                        buttonText = "OK"
                    )
                )
            }
            return
        }

        val request = JoinUsRequest(
            name = name.trim(),
            email = email.trim(),
            phoneNumber = phoneNumber.trim(),
            city = city.trim(),
            state = state.trim(),
            insuranceCompany = company.trim(),
            role = role.trim()
        )

        interactor.submitJoinUsRequest(request)
            .onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        setState {
                            copy(progressBarState = dataState.progressBarState)
                        }
                    }

                    is DataState.Data -> {
                        setState {
                            copy(
                                registrationSuccess = dataState.data,
                                progressBarState = ProgressBarState.Idle
                            )
                        }
                        dataState.data?.let { data ->
                            setAction { JoinUsAction.RegistrationSuccess(data) }
                        }
                    }

                    is DataState.Error -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog -> {
                                setAction { JoinUsAction.ShowDialog(dataState.uiComponent) }
                            }
                            is UIComponent.ErrorData -> {
                                setAction { JoinUsAction.ShowErrorScreen(dataState.uiComponent) }
                            }
                            is UIComponent.Snackbar -> {
                                setAction { JoinUsAction.ShowSnackbar(dataState.uiComponent) }
                            }
                            else -> {}
                        }
                    }

                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }
}