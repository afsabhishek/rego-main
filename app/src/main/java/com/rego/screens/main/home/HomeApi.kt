package com.rego.screens.main.home

import com.rego.CommonResponse

interface HomeApi {
    fun getHomeData(): CommonResponse<String> //Replace this by actual response class
}