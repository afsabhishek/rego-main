package com.rego.screens.main.home

import com.rego.screens.base.ViewSingleAction

sealed class HomeAction : ViewSingleAction {
    object NavigateToProfile : HomeAction()
    object NavigateToNotifications : HomeAction()
    data class NavigateToOrderDetails(val orderId: String) : HomeAction()
    data class NavigateToOrderList(val orderType: String) : HomeAction()
    object NavigateToRaiseRequest : HomeAction()
    object NavigateToSearch : HomeAction()
    data class ShowError(val message: String) : HomeAction()
    object RetryLoadData : HomeAction()
    data class ShowSnackbar(val message: String) : HomeAction()
}