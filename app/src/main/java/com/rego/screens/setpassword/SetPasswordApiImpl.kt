package com.rego.screens.setpassword

class SetPasswordApiImpl : SetPasswordApi {
    override suspend fun setPassword(userId: String, password: String): Boolean {
        // For stub, accept any 8+ char password
        return password.length >= 8
    }
}
