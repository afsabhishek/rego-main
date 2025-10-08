package com.rego.screens.orderdetails

import com.rego.R
import com.rego.screens.components.OrderData

class OrderDetailsApiImpl : OrderDetailsApi {
    override suspend fun getOrderList(): Map<Int, List<OrderData>> = mapOf(
        0 to listOf(
            OrderData(
                "12042501",
                "Work In Progress",
                "Hyundai i20, 2023",
                "21/04/25",
                "Prem Motors",
                "Sector 18, Gurgoan,\nHaryana "
            ),
            OrderData("12042502", "Work In Progress", "Hyundai i20, 2023", "22/04/25")
        ),
        1 to listOf(
            OrderData("12042521", "Pickup Aligned", "Hyundai i20, 2023", "12/05/25")
        ),
        2 to listOf(
            OrderData("12042531", "Part Delivered", "Honda City, 2022", "15/05/25")
        ),
        3 to listOf(
            OrderData("12042541", "Ready for Delivery", "Toyota Camry, 2021", "18/05/25")
        )
    )

    override suspend fun getOrderDetails(orderId: String): OrderDetails = OrderDetails(
        orderId = orderId,
        carName = "Hyundai Creta, 2023",
        regNumber = "HR 26 EX 9342",
        advisorName = "Suresh Singh",
        advisorContact = "9954332342",
        dealerName = "Prem Motors",
        dealerLocation = "Sector 18, Gurgoan, Haryana",
        claimNumber = "245577545242",
        rejectionReason = "Pickup denied by Workshop",
        partType = "Alloy Wheel",
        partPhotos = List(8) { R.drawable.part_photo }
    )
}

// Custom data class for order details

data class OrderDetails(
    val orderId: String,
    val carName: String,
    val regNumber: String,
    val advisorName: String,
    val advisorContact: String,
    val dealerName: String,
    val dealerLocation: String,
    val claimNumber: String,
    val rejectionReason: String,
    val partType: String,
    val partPhotos: List<Int>
)
