package com.rego.screens.orderdetails.data

data class OrderDetails(
    val orderId: String,
    val carName: String,
    val regNumber: String,
    val advisorName: String,
    val advisorContact: String,
    val dealerName: String,
    val dealerLocation: String,
    val claimNumber: String,
    val rejectionReason: String?,      // backend not present (nullable)
    val partType: String,
    val partPhotos: List<Int> = emptyList()
)