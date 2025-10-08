package com.rego.screens.main.home

import com.rego.CommonResponse

class HomeApiImpl(
    /** ktor http client */
) : HomeApi {
    override fun getHomeData(): CommonResponse<String> {
        return CommonResponse(data = "home data", status = true, message = "success")
    }
}