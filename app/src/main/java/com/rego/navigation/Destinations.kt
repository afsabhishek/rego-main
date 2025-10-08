package com.rego.navigation

sealed class Destinations(val route: String) {
    object Splash : Destinations("splash")
    object LoginOptions : Destinations("login_options")
    object Login : Destinations("login")
    object Signup : Destinations("signup")

    // nested under main
    object Home {
        const val route = "home"
        const val routeWithArgs = "home?userId={userId}&firebaseUid={firebaseUid}"

        fun createRoute(userId: String?, firebaseUid: String?): String {
            return if (!userId.isNullOrBlank() && !firebaseUid.isNullOrBlank()) {
                "home?userId=$userId&firebaseUid=$firebaseUid"
            } else {
                route
            }
        }
    }
    object Profile : Destinations("profile")
    object Notification : Destinations("notification")
    object ResetPassword : Destinations("reset_password")
    object RaiseRequest : Destinations("raise_request")
    object OrdersList : Destinations("orders_list/{type}") {
        fun createRoute(type: String) = "orders_list/$type"
    }
    object OrderDetails : Destinations("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
}