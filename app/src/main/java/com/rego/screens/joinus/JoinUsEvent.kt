package com.rego.screens.joinus

import com.rego.screens.base.ViewEvent
import com.rego.screens.joinus.data.InsuranceCompany

sealed class JoinUsEvent : ViewEvent {
    object LoadRegisterData : JoinUsEvent()  // ✅ Changed from LoadInsuranceCompanies
    data class SelectInsuranceCompany(val company: InsuranceCompany) : JoinUsEvent()
    data class SelectState(val state: String) : JoinUsEvent()  // ✅ New event
    data class SubmitRegistration(
        val name: String,
        val email: String,
        val phoneNumber: String,
        val city: String,
        val state: String,
        val company: String,
        val role: String
    ) : JoinUsEvent()
    object RetryLoadingData : JoinUsEvent()  // ✅ Changed from RetryLoadingCompanies
}