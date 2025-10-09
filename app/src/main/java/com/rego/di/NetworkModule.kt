package com.rego.di

import com.rego.network.KtorClient
import com.rego.util.UserPreferences
import org.koin.dsl.module

val networkModule = module {
    single {
        KtorClient(
            userPreferences = get<UserPreferences>()
        )
    }
}