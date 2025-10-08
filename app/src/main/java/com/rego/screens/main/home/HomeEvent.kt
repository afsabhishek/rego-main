package com.rego.screens.main.home

import com.rego.screens.base.ViewEvent

sealed class HomeEvent : ViewEvent {
    object Init : HomeEvent()
}