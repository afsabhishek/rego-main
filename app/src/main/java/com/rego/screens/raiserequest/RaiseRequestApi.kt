package com.rego.screens.raiserequest

interface RaiseRequestApi {
    suspend fun getFormOptions(): RaiseRequestFormOptions
    suspend fun submitRequest(data: RaiseRequestFormData): Boolean
}
