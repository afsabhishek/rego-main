package com.rego.screens.raiserequest

class RaiseRequestApiImpl : RaiseRequestApi {
    override suspend fun getFormOptions(): RaiseRequestFormOptions = RaiseRequestFormOptions(
        carMakes = listOf("Toyota", "Honda", "Maruti Suzuki", "Hyundai", "Mahindra", "Tata"),
        carModels = listOf("Camry", "Civic", "Swift", "i20", "XUV700", "Nexon"),
        fuelTypes = listOf("Petrol", "Diesel", "CNG", "Electric", "Hybrid"),
        carVariants = listOf("Base", "Mid", "Top", "VXi", "ZXi", "Alpha"),
        dealerLocations = listOf("Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Pune"),
        policyTypes = listOf(
            "Comprehensive",
            "Third Party",
            "Zero Depreciation",
            "Return to Invoice"
        ),
        partTypes = listOf(
            PartType(
                "alloy_wheels",
                "Alloy wheels",
                com.rego.R.drawable.alloy_wheel
            ),
            PartType(
                "headlamps",
                "Headlamps",
                com.rego.R.drawable.car_light
            ),
            PartType(
                "plastic_repair",
                "Plastic repair",
                com.rego.R.drawable.car_seat
            )
        )
    )

    override suspend fun submitRequest(data: RaiseRequestFormData): Boolean = true
}

// Data classes for form options and request data

data class RaiseRequestFormOptions(
    val carMakes: List<String>,
    val carModels: List<String>,
    val fuelTypes: List<String>,
    val carVariants: List<String>,
    val dealerLocations: List<String>,
    val policyTypes: List<String>,
    val partTypes: List<PartType>
)

data class RaiseRequestFormData(
    val selectedCarMake: String,
    val selectedCarModel: String,
    val selectedFuelType: String,
    val selectedCarVariant: String,
    val selectedDealerLocation: String,
    val selectedPolicyType: String,
    val dealerName: String,
    val advisorName: String,
    val advisorContactNumber: String,
    val claimNumber: String,
    val carRegNumber: String,
    val makeYear: String,
    val isInventoryPickup: Boolean,
    val selectedPartType: String,
    val images: List<String>
)
