package com.rego.screens.main.profile

import com.rego.CommonResponse

class ProfileApiImpl : ProfileApi {
    override fun getProfile(): CommonResponse<ProfileData> {
        return CommonResponse(
            data = ProfileData(
                name = "Ayush Singh",
                phone = "+91 701018897",
                email = "ayush12@icici.com",
                customerId = "ICI01"
            ),
            status = true,
            message = "success"
        )
    }
}