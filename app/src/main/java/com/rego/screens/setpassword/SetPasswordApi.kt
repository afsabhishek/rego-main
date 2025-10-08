package com.rego.screens.setpassword

interface SetPasswordApi {
    suspend fun setPassword(userId: String, password: String): Boolean
}
