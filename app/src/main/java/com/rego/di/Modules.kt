package com.rego.di

import com.rego.screens.loginoption.LoginOptionViewModel
import com.rego.screens.main.home.HomeApi
import com.rego.screens.main.home.HomeApiImpl
import com.rego.screens.main.home.HomeInteractor
import com.rego.screens.main.home.HomeViewModel
import com.rego.screens.joinus.JoinUsApi
import com.rego.screens.joinus.JoinUsApiImpl
import com.rego.screens.joinus.JoinUsInteractor
import com.rego.screens.joinus.JoinUsViewModel
import com.rego.screens.mobileverification.MobileVerificationApi
import com.rego.screens.mobileverification.MobileVerificationApiImpl
import com.rego.screens.mobileverification.MobileVerificationInteractor
import com.rego.screens.mobileverification.MobileVerificationViewModel
import com.rego.screens.notifications.NotificationApi
import com.rego.screens.notifications.NotificationApiImpl
import com.rego.screens.notifications.NotificationInteractor
import com.rego.screens.notifications.NotificationViewModel
import com.rego.screens.orderdetails.OrderDetailsApi
import com.rego.screens.orderdetails.OrderDetailsApiImpl
import com.rego.screens.orderdetails.OrderDetailsInteractor
import com.rego.screens.orderdetails.OrderDetailsViewModel
import com.rego.screens.raiserequest.RaiseRequestApi
import com.rego.screens.raiserequest.RaiseRequestApiImpl
import com.rego.screens.raiserequest.RaiseRequestInteractor
import com.rego.screens.raiserequest.RaiseRequestViewModel
import com.rego.screens.setpassword.SetPasswordApi
import com.rego.screens.setpassword.SetPasswordApiImpl
import com.rego.screens.setpassword.SetPasswordInteractor
import com.rego.screens.setpassword.SetPasswordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { LoginOptionViewModel() }

    factory<HomeApi> { HomeApiImpl() }
    factory { HomeInteractor(get()) }
    viewModel { HomeViewModel(get()) }

    // Notifications
    factory<NotificationApi> { NotificationApiImpl() }
    factory { NotificationInteractor(get()) }
    viewModel { NotificationViewModel(get()) }

    // Order Details
    factory<OrderDetailsApi> { OrderDetailsApiImpl() }
    factory { OrderDetailsInteractor(get()) }
    viewModel { OrderDetailsViewModel(get()) }

    // Raise a Request
    factory<RaiseRequestApi> { RaiseRequestApiImpl() }
    factory { RaiseRequestInteractor(get()) }
    viewModel { RaiseRequestViewModel(get()) }

    // Set password
    factory<SetPasswordApi> { SetPasswordApiImpl() }
    factory { SetPasswordInteractor(get()) }
    viewModel { SetPasswordViewModel(get()) }

    factory<MobileVerificationApi> {
        MobileVerificationApiImpl(
            ktorClient = get() // Injects KtorClient from networkModule
        )
    }

    // 2. Interactor (Business Logic Layer)
    factory {
        MobileVerificationInteractor(
            api = get() // Injects MobileVerificationApi (which is MobileVerificationApiImpl)
        )
    }

    // 3. ViewModel (Presentation Layer)
    viewModel {
        MobileVerificationViewModel(
            interactor = get() // Injects MobileVerificationInteractor
        )
    }

    // Join Us

    factory<JoinUsApi> {
        JoinUsApiImpl(
            ktorClient = get() // Injects KtorClient from networkModule
        )
    }

    factory {
        JoinUsInteractor(
            api = get()
        )
    }

    viewModel {
        JoinUsViewModel(
            interactor = get()
        )
    }
}