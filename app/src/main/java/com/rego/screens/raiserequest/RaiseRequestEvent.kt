package com.rego.screens.raiserequest

import com.rego.screens.base.ViewEvent

sealed class RaiseRequestEvent : ViewEvent {
    object Init : RaiseRequestEvent()
    data class FieldChanged(val field: String, val value: Any) : RaiseRequestEvent()
    data class SubmitRequest(val data: RaiseRequestFormData) : RaiseRequestEvent()
}
