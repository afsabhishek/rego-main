package com.rego.screens.main.profile

import com.rego.CommonResponse
import com.rego.screens.main.profile.data.ProfileResponse

interface ProfileApi {
    suspend fun getUserProfile(authToken: String): ProfileResponse
}
