package com.rego.screens.raiserequest

import com.rego.screens.raiserequest.data.CreateLeadRequest
import com.rego.screens.raiserequest.data.CreateLeadResponse
import com.rego.screens.raiserequest.data.PartTypesResponse
import com.rego.screens.raiserequest.data.VehicleMakesResponse
import com.rego.screens.raiserequest.data.VehicleModelsResponse
import com.rego.screens.raiserequest.data.VehicleVariantsResponse
import com.rego.screens.raiserequest.data.WorkshopDealersResponse
import com.rego.screens.raiserequest.data.WorkshopLocationsResponse
import java.io.File

interface RaiseRequestApi {
    // Vehicle related APIs
    suspend fun getVehicleMakes(): VehicleMakesResponse
    suspend fun getVehicleModels(make: String): VehicleModelsResponse
    suspend fun getVehicleVariants(make: String, model: String, fuelType: String): VehicleVariantsResponse

    // Workshop related APIs
    suspend fun getWorkshopLocations(make: String): WorkshopLocationsResponse
    suspend fun getWorkshopDealers(make: String, location: String): WorkshopDealersResponse

    // Part types API
    suspend fun getPartTypes(): PartTypesResponse

    // Create lead API
    suspend fun createLead(
        request: CreateLeadRequest,
        images: List<File>,
        authToken: String
    ): CreateLeadResponse
}