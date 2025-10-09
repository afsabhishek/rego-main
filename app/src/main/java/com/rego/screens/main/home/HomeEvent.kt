package com.rego.screens.main.home

import com.rego.screens.base.ViewEvent

sealed class HomeEvent : ViewEvent {
    object Init : HomeEvent()
    object RefreshData : HomeEvent()
    data class FilterLeads(val status: String?) : HomeEvent()
}