package com.rego

import android.app.Application
import com.rego.di.appModule
import com.rego.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                appModule
            )
        }
    }
}