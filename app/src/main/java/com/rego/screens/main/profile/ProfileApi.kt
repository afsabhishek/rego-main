package com.rego.screens.main.profile

import com.rego.CommonResponse

data class ProfileData(
    val name: String,
    val phone: String,
    val email: String,
    val customerId: String
)

interface ProfileApi {
    fun getProfile(): CommonResponse<ProfileData>
}