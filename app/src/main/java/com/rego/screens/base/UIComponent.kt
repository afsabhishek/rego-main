package com.rego.screens.base

sealed class UIComponent {

    data class Toast(
        var title: String,
        var message: String
    ) : UIComponent()

    data class Dialog(
        var title: String,
        var message: String
    ) : UIComponent()

    data class None(
        val message: String,
    ) : UIComponent()

    data class Snackbar(
        var message: String,
        var buttonText: String,
    ) : UIComponent()

    data class ErrorData(
        var title: String,
        var message: String,
        var buttonText: String,
    ) : UIComponent()


}