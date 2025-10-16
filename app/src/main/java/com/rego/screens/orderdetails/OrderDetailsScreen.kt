package com.rego.screens.orderdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.components.DashedDivider
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontSemiBoldMontserrat
import com.rego.ui.theme.fontSemiBoldPoppins
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(orderId: String, onBack: () -> Unit) {
    val viewModel: OrderDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.setEvent(OrderDetailsEvent.LoadLeadDetails(orderId))
    }

    val lead = state.selectedLead

    DefaultScreenUI(progressBarState = state.progressBarState) { paddingValues ->
        Spacer(modifier = Modifier.size(paddingValues.calculateTopPadding()))

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 14.dp, bottom = 14.dp, end = 14.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color1A1A1A_90(),
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Order details",
                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                color = Color1A1A1A_90(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.5f))
                .height(2.dp)
                .shadow(1.dp)
        )

        if (state.progressBarState == com.rego.screens.base.ProgressBarState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && lead == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.error ?: "Failed to load order details",
                    color = Color.Gray,
                    style = fontSemiBoldPoppins().copy(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.setEvent(OrderDetailsEvent.RetryLoadDetails) }
                ) {
                    Text("Retry")
                }
            }
        } else if (lead != null) {
            OrderDetailsContent(lead = lead)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No order details available", color = Color.Gray)
            }
        }
    }
}

@Composable
fun OrderDetailsContent(lead: LeadsResponse.LeadsData.Lead) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        // Order ID
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Order ID: ",
                style = fontSemiBoldPoppins().copy(
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            )
            Text(
                text = lead.leadId,
                style = fontSemiBoldPoppins().copy(
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.9f)
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))
        DashedDivider()
        Spacer(Modifier.height(24.dp))

        // Part Type
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Part Type:",
                style = fontSemiBoldPoppins().copy(
                    fontSize = 14.sp,
                    color = Color1A1A1A_90()
                )
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = getPartTypeIcon(lead.partType)),
                contentDescription = lead.partType,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = lead.partType,
                style = fontSemiBoldPoppins().copy(
                    fontSize = 12.sp,
                    color = Color1A1A1A_90()
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        // Part Photos Section (if available in future API updates)
        // For now, showing placeholder since images aren't in the current API response
        Text(
            text = "Part Photos:",
            style = fontSemiBoldPoppins().copy(fontSize = 14.sp, color = Color1A1A1A_90())
        )
        Spacer(Modifier.height(8.dp))

        // Show vehicle image if available
        lead.vehicle.image?.let { imageUrl ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Vehicle Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.part_photo),
                error = painterResource(id = R.drawable.part_photo)
            )
        } ?: run {
            // Placeholder if no image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No images available",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Order Summary Card
        OrderSummaryCard(lead)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun OrderSummaryCard(lead: LeadsResponse.LeadsData.Lead) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Car Details
            TwoColumnLabelRow(
                label1 = "Car Name",
                label2 = "Car Registration Number"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = "${lead.vehicle.make} ${lead.vehicle.model}, ${lead.makeYear}",
                value2 = lead.registrationNumber
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Advisor Details
            TwoColumnLabelRow(
                label1 = "Advisor Name",
                label2 = "Advisor Contact Number"
            )
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    lead.advisor.name,
                    style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90())
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = "Call",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        lead.advisor.contact,
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 12.sp,
                            color = Color1A1A1A_90()
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dealer Details
            TwoColumnLabelRow(
                label1 = "Dealer Name",
                label2 = "Dealer Location"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = lead.dealer.name,
                value2 = lead.dealer.location
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Claim Number and Status
            TwoColumnLabelRow(
                label1 = "Claim Number",
                label2 = "Status"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = lead.claimNumber,
                value2 = formatStatus(lead.status),
                value2Color = getStatusColor(lead.status)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Additional Details
            TwoColumnLabelRow(
                label1 = "Policy Type",
                label2 = "Inventory Pickup"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = lead.policyType,
                value2 = if (lead.inventoryPickUp) "Yes" else "No"
            )
        }
    }
}

@Composable
fun TwoColumnLabelRow(
    label1: String,
    label2: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label1,
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_60())
        )
        Text(
            text = label2,
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_60())
        )
    }
}

@Composable
fun TwoColumnValueRow(
    value1: String,
    value2: String,
    value2Color: Color = Color1A1A1A_90()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value1,
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90()),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value2,
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = value2Color),
            modifier = Modifier.weight(1f)
        )
    }
}

// Helper functions
fun getPartTypeIcon(partType: String): Int {
    return if (partType.lowercase() == "alloy wheel" || partType.lowercase() == "alloy wheels") R.drawable.alloy_wheel
    else if (partType.lowercase() == "headlamp" || partType.lowercase() == "headlamps") R.drawable.car_light
    else if (partType.lowercase() == "plastic repair") R.drawable.car_bumper
    else if (partType.lowercase() == "leather & fabric repair") R.drawable.car_seat
    else R.drawable.alloy_wheel
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "COMPLETED" -> Color(0xFF11CA3C)
        "NOT_REPAIRABLE" -> Color(0xFFE7503D)
        "WORK_IN_PROGRESS" -> Color(0xFFF8751E)
        "APPROVED" -> Color(0xFF11CA3C)
        else -> Color1A1A1A_90()
    }
}

@Preview
@Composable
fun PreviewOrderDetailsScreen() {
    NativeAndroidBaseArchitectureTheme {
        OrderDetailsScreen(orderId = "12345", onBack = {})
    }
}