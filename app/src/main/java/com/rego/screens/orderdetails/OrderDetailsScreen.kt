package com.rego.screens.orderdetails

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.components.DashedDivider
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontSemiBoldMontserrat
import com.rego.ui.theme.fontSemiBoldPoppins
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(orderId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: OrderDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.setEvent(OrderDetailsEvent.LoadOrderDetails(orderId))
    }
    val details = state.selectedOrderDetails

    DefaultScreenUI(progressBarState = state.progressBarState) { paddingValues ->
        Spacer(modifier = Modifier.size(paddingValues.calculateTopPadding()))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    start = 14.dp,
                    bottom = 14.dp,
                    end = 14.dp
                )
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
        Spacer(modifier = Modifier.size(18.dp))
        if (details == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading...", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Order ID: ",
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    )
                    Text(
                        text = details.orderId,
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.9f)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                DashedDivider()
                Spacer(Modifier.height(24.dp))
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
                        painter = painterResource(id = R.drawable.alloy_wheel), // Could use mapped icon
                        contentDescription = details.partType,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = details.partType,
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 12.sp,
                            color = Color1A1A1A_90()
                        )
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Part Photos:",
                    style = fontSemiBoldPoppins().copy(fontSize = 14.sp, color = Color1A1A1A_90())
                )
                Spacer(Modifier.height(8.dp))
                PartPhotoGrid(images = details.partPhotos)
                Spacer(Modifier.height(8.dp))
                OrderSummaryCard(details)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    }
}

@Composable
fun PartPhotoGrid(images: List<Int>) {
    Column {
        for (row in 0..1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (col in 0..3) {
                    val imageRes = images.getOrNull(row * 4 + col)
                    if (imageRes != null) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Part Photo",
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun TwoColumnLabelRow(
    label1: String,
    label2: String,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Int = 14,
    color: Color = Color.Gray
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
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90())
        )
        Text(
            text = value2,
            style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = value2Color)
        )
    }
}

@Composable
fun OrderSummaryCard(details: OrderDetails) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TwoColumnLabelRow(
                label1 = "Car Name",
                label2 = "Car Registration Number"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = details.carName,
                value2 = details.regNumber
            )
            Spacer(modifier = Modifier.height(14.dp))
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
                    details.advisorName,
                    style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90())
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        details.advisorContact,
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 12.sp,
                            color = Color1A1A1A_90()
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            TwoColumnLabelRow(
                label1 = "Dealer Name",
                label2 = "Dealer Location"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = details.dealerName,
                value2 = details.dealerLocation
            )
            Spacer(modifier = Modifier.height(14.dp))
            TwoColumnLabelRow(
                label1 = "Claim Number",
                label2 = "Reason for rejection"
            )
            Spacer(Modifier.height(2.dp))
            TwoColumnValueRow(
                value1 = details.claimNumber,
                value2 = details.rejectionReason,
                value2Color = Color(0xFFE73C33)
            )
        }
    }
}

@Preview
@Composable
fun PreviewOrderCard() {
    NativeAndroidBaseArchitectureTheme {
        OrderDetailsScreen(orderId = "12345", onBack = {})
    }
}