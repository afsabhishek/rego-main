package com.rego.screens.raiserequest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaiseRequestFormOptionsResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: FormOptionsData? = null
) {
    @Serializable
    data class FormOptionsData(
        @SerialName("message")
        val message: String = "",
        @SerialName("carMakes")
        val carMakes: List<String>,
        @SerialName("carModels")
        val carModels: List<String>,
        @SerialName("fuelTypes")
        val fuelTypes: List<String>,
        @SerialName("carVariants")
        val carVariants: List<String>,
        @SerialName("dealerLocations")
        val dealerLocations: List<String>,
        @SerialName("policyTypes")
        val policyTypes: List<String>,
        @SerialName("partTypes")
        val partTypes: List<PartType>
    ) {

        @Serializable
        data class PartType(
            @SerialName("id")
            val id: String,
            @SerialName("name")
            val name: String,
            @SerialName("icon")
            val iconName: String
        ) {
            fun getIconResource(): Int {
                return when (iconName) {
                    "alloy_wheel" -> com.rego.R.drawable.alloy_wheel
                    "car_light" -> com.rego.R.drawable.car_light
                    "car_seat" -> com.rego.R.drawable.car_seat
                    "car_bumper" -> com.rego.R.drawable.car_bumper
                    else -> com.rego.R.drawable.alloy_wheel
                }
            }
        }
    }
}