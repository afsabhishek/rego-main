package com.rego.screens.main.home

import com.rego.screens.base.ViewEvent

sealed class HomeEvent : ViewEvent {
    object Init : HomeEvent()
    object RefreshData : HomeEvent()
    data class FilterLeads(val status: String?) : HomeEvent()
    object LoadMoreLeads : HomeEvent()
    data class SearchLeads(val query: String) : HomeEvent()
    object ClearSearch : HomeEvent()
    object RetryLoadStats : HomeEvent()
    object RetryLoadLeads : HomeEvent()
    data class OnCardClick(val cardType: String) : HomeEvent()
    data class OnOrderClick(val orderId: String) : HomeEvent()
    object OnProfileClick : HomeEvent()
    object OnNotificationClick : HomeEvent()
    object OnRaiseRequestClick : HomeEvent()
    object OnSearchClick : HomeEvent()
}