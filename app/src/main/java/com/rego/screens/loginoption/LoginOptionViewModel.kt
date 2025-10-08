package com.rego.screens.loginoption

import com.rego.screens.base.BaseViewModel

class LoginOptionViewModel :
    BaseViewModel<LoginOptionEvent, LoginOptionViewState, LoginOptionAction>() {

    override fun setInitialState() = LoginOptionViewState()

    override fun onTriggerEvent(event: LoginOptionEvent) {
        when (event) {
            is LoginOptionEvent.Init -> {
                init()
            }
        }
    }

    private fun init() {

    }
}