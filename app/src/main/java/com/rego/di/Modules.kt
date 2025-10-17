package com.rego.di

import com.google.firebase.auth.FirebaseAuth
import com.rego.auth.FirebaseAuthManager
import com.rego.screens.auth.AuthInteractor
import com.rego.screens.auth.AuthApiImpl
import com.rego.screens.loginoption.LoginOptionViewModel
import com.rego.screens.main.home.HomeApi
import com.rego.screens.main.home.HomeApiImpl
import com.rego.screens.main.home.HomeInteractor
import com.rego.screens.main.home.HomeViewModel
import com.rego.screens.main.profile.ProfileApi
import com.rego.screens.main.profile.ProfileApiImpl
import com.rego.screens.main.profile.ProfileInteractor
import com.rego.screens.main.profile.ProfileViewModel
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
import com.rego.util.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // User Preferences
    single { UserPreferences(androidContext()) }

    // Firebase Auth
    single { FirebaseAuth.getInstance() }
    single { FirebaseAuthManager(get()) }

    // Auth
    factory<com.rego.screens.auth.AuthApi> {
        AuthApiImpl(
            ktorClient = get()
        )
    }
    factory {
        AuthInteractor(
            authApi = get(),
            userPreferences = get(),
            firebaseAuthManager = get()
        )
    }
    viewModel {
        com.rego.screens.splash.SplashViewModel(
            authInteractor = get()
        )
    }

    viewModel { LoginOptionViewModel() }

    factory<HomeApi> {
        HomeApiImpl(
            ktorClient = get()
        )
    }
    factory {
        HomeInteractor(
            homeApi = get(),
            userPreferences = get()
        )
    }
    viewModel {
        HomeViewModel(
            homeInteractor = get(),
            profileInteractor = get(),
            userPreferences = get()
        )
    }

    // Profile - Updated with real API
    factory<ProfileApi> {
        ProfileApiImpl(
            ktorClient = get(),
            userPreferences = get()
        )
    }
    factory {
        ProfileInteractor(
            api = get(),
            userPreferences = get()
        )
    }
    viewModel { ProfileViewModel(get(), userPreferences = get()) }

    // Notifications
    factory<NotificationApi> { NotificationApiImpl() }
    factory { NotificationInteractor(get()) }
    viewModel { NotificationViewModel(get()) }

// Order
// Order Details - Updated to use real API
    factory<OrderDetailsApi> {
        OrderDetailsApiImpl(
            ktorClient = get()
        )
    }
    factory {
        OrderDetailsInteractor(
            api = get(),
            userPreferences = get()
        )
    }
    viewModel { OrderDetailsViewModel(get()) }

    // Set password
    factory<SetPasswordApi> { SetPasswordApiImpl() }
    factory { SetPasswordInteractor(get()) }
    viewModel { SetPasswordViewModel(get()) }

    factory<MobileVerificationApi> {
        MobileVerificationApiImpl(
            ktorClient = get()
        )
    }

    factory {
        MobileVerificationInteractor(
            api = get()
        )
    }

    viewModel {
        MobileVerificationViewModel(
            interactor = get(),
            userPreferences = get(),
            authInteractor = get(),
            firebaseAuthManager = get()
        )
    }

    // Join Us
    factory<JoinUsApi> {
        JoinUsApiImpl(
            ktorClient = get()
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

    factory<RaiseRequestApi> {
            RaiseRequestApiImpl(
            ktorClient = get()
        )
    }

    factory {
        RaiseRequestInteractor(
            api = get(),
            userPreferences = get(),
            context = androidContext()
        )
    }

    viewModel {
        RaiseRequestViewModel(
            interactor = get()
        )
    }
}