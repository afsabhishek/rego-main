package com.rego.di

import com.rego.network.KtorClient
import org.koin.dsl.module

val networkModule = module {
    single { KtorClient() }
}