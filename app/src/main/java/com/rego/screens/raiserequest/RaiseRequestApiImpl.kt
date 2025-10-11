package com.rego.screens.raiserequest

import com.rego.network.KtorClient
import com.rego.network.NetworkConfig
import com.rego.screens.raiserequest.data.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File

class RaiseRequestApiImpl(
    private val ktorClient: KtorClient
) : RaiseRequestApi {

    companion object {
        private const val BASE_URL = NetworkConfig.BASE_URL

        // API endpoints
        private const val VEHICLES_MAKES = "/vehicles/makes"
        private const val VEHICLES_MODELS = "/vehicles/models"
        private const val VEHICLES_VARIANTS = "/vehicles/variants"
        private const val WORKSHOPS_LOCATIONS = "/workshops/locations"
        private const val WORKSHOPS_DEALERS = "/workshops/dealers"
        private const val REFERENCE_PARTS = "/reference/parts"
        private const val LEADS = "/leads"
    }

    override suspend fun getVehicleMakes(): VehicleMakesResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$VEHICLES_MAKES")
            }
            response.body<VehicleMakesResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            VehicleMakesResponse(
                success = false,
                data = null,
                message = "Failed to fetch vehicle makes: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getVehicleModels(make: String): VehicleModelsResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$VEHICLES_MODELS")
                parameter("make", make)
            }
            response.body<VehicleModelsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            VehicleModelsResponse(
                success = false,
                data = null,
                message = "Failed to fetch vehicle models: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getVehicleVariants(
        make: String,
        model: String,
        fuelType: String
    ): VehicleVariantsResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$VEHICLES_VARIANTS")
                parameter("make", make)
                parameter("model", model)
                parameter("fuelType", fuelType)
            }
            response.body<VehicleVariantsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            VehicleVariantsResponse(
                success = false,
                data = null,
                message = "Failed to fetch vehicle variants: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getWorkshopLocations(make: String): WorkshopLocationsResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$WORKSHOPS_LOCATIONS")
                parameter("make", make)
            }
            response.body<WorkshopLocationsResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            WorkshopLocationsResponse(
                success = false,
                data = null,
                message = "Failed to fetch workshop locations: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getWorkshopDealers(
        make: String,
        location: String
    ): WorkshopDealersResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$WORKSHOPS_DEALERS")
                parameter("make", make)
                parameter("location", location)
            }
            response.body<WorkshopDealersResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            WorkshopDealersResponse(
                success = false,
                data = null,
                message = "Failed to fetch workshop dealers: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun getPartTypes(): PartTypesResponse {
        return try {
            val response = ktorClient.client.get {
                url("$BASE_URL$REFERENCE_PARTS")
            }
            response.body<PartTypesResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            PartTypesResponse(
                success = false,
                data = null,
                message = "Failed to fetch part types: ${e.localizedMessage}"
            )
        }
    }

    override suspend fun createLead(
        request: CreateLeadRequest,
        images: List<File>,
        authToken: String
    ): CreateLeadResponse {
        return try {
            val response = ktorClient.client.submitFormWithBinaryData(
                url = "$BASE_URL$LEADS",
                formData = formData {
                    // Add text fields
                    append("partType", request.partType)
                    append("vehicleId", request.vehicleId)
                    append("registrationNumber", request.registrationNumber)
                    append("makeYear", request.makeYear.toString())
                    append("inventoryPickUp", request.inventoryPickUp.toString())
                    append("workshopId", request.workshopId)
                    append("advisorName", request.advisorName)
                    append("advisorContact", request.advisorContact)
                    append("policyType", request.policyType)
                    append("claimNumber", request.claimNumber)

                    // Add images
                    images.forEach { file ->
                        append("images", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/${file.extension}")
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                        })
                    }
                }
            ) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }

            response.body<CreateLeadResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            CreateLeadResponse(
                success = false,
                message = "Failed to create lead: ${e.localizedMessage}",
                data = null
            )
        }
    }
}