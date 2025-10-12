package com.rego.screens.joinus

import com.rego.CommonResponse
import com.rego.screens.joinus.data.JoinUsRequest
import com.rego.screens.joinus.data.JoinUsResponse
import com.rego.screens.joinus.data.RegisterReferenceResponse

interface JoinUsApi {
    /**
     * Get registration reference data including insurance companies, states, and cities
     */
    suspend fun getRegisterReference(): RegisterReferenceResponse

    /**
     * Submit join us registration request
     */
    suspend fun submitJoinUsRequest(
        request: JoinUsRequest
    ): CommonResponse<JoinUsResponse.JoinUsData>
}