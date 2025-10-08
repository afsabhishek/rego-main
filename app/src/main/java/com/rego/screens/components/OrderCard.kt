package com.rego.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_16
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.fontBoldPoppins
import com.rego.ui.theme.fontMediumPoppins
import com.rego.ui.theme.fontSemiBoldPoppins

data class OrderData(
    val orderId: String,
    val status: String,
    val carMake: String? = null,
    val deliveryDate: String? = null,
    val dealerName: String? = null,
    val dealerLocation: String? = null
)

@Composable
fun OrderCard(
    order: OrderData,
    isExpanded: Boolean = false,
    onToggleExpanded: () -> Unit = {},
    onCardClick: () -> Unit = {},
    fromOrderListing: Boolean = false,
    orderType: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color1A1A1A_16(), RoundedCornerShape(16.dp))
            .padding(top = 13.dp)
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order ID",
                        style = fontMediumPoppins().copy(fontSize = 12.sp),
                        color = Color1A1A1A_60()
                    )
                    Text(
                        text = order.orderId,
                        style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                        color = Color.Black.copy(alpha = 0.87f)
                    )
                }
                StatusBadge(status = order.status)
            }

            // Expanded info
            if (isExpanded && order.carMake != null && order.deliveryDate != null) {
                Spacer(modifier = Modifier.height(12.dp))
                DashedDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = Color(0xFFEBEBEB),
                    gapLength = 10f,
                    dashLength = 20f
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Car Make",
                            style = fontMediumPoppins().copy(fontSize = 12.sp),
                            color = Color1A1A1A_60()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = order.carMake,
                            style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                    }
                    Column {
                        Text(
                            text = "Target Delivery Date",
                            style = fontMediumPoppins().copy(fontSize = 12.sp),
                            color = Color1A1A1A_60()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = order.deliveryDate,
                            style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                    }
                }
                if (orderType == "Not Repairable") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Dealer Name",
                                style = fontMediumPoppins().copy(fontSize = 12.sp),
                                color = Color1A1A1A_60()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = order.dealerName ?: "N/A",
                                style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                                color = Color.Black.copy(alpha = 0.87f)
                            )
                        }
                        Column {
                            Text(
                                text = "Dealer Location",
                                style = fontMediumPoppins().copy(fontSize = 12.sp),
                                color = Color1A1A1A_60()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = order.dealerLocation ?: "N/A",
                                style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                                color = Color.Black.copy(alpha = 0.87f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Part Photos:",
                        style = fontSemiBoldPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90())
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        repeat(4) {
                            Image(
                                painter = painterResource(id = R.drawable.part_photo),
                                contentDescription = "Part Photo",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }
            }

            if (fromOrderListing.not() && order.carMake != null && order.deliveryDate != null) {
                Spacer(modifier = Modifier.height(15.dp))
                // View More/View Less label
                Row(
                    modifier = Modifier
                        .clickable { onToggleExpanded() }
                        .wrapContentWidth()
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "View Less" else "View More",
                        style = fontSemiBoldPoppins().copy(fontSize = 12.sp),
                        color = Color(0xFF00954D)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = "Expand",
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .rotate(if (isExpanded) 180f else 0f),
                        tint = Color(0xFF00954D)
                    )
                }
            } else {
// View Order button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(
                            Color(0x1F00C061),
                            RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                        )
                        .clickable { onCardClick() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View Order >>",
                        style = fontSemiBoldPoppins().copy(fontSize = 12.sp),
                        color = Color00954D
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(13.dp),
                color = getBorderColorForStatus(status)
            )
            .background(
                getBgColorForStatus(status),
                RoundedCornerShape(13.dp)
            )
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Text(
            text = status,
            style = fontBoldPoppins().copy(fontSize = 10.sp),
            color = getTextColorForStatus(status)
        )
    }
}

fun getTextColorForStatus(orderStatus: String): Color {
    return when (orderStatus) {
        "Work In Progress" -> Color(0xFFF8751E)
        "Pickup Aligned" -> Color(0xFFF81EF8)
        "Part Delivered" -> Color(0xFF11CA3C)
        "Pickup Done" -> Color(0xFFD69B0C)
        "Invoice Generated" -> Color(0xFA1873E4)
        "Ready for Delivery" -> Color(0xFF721EF8)
        else -> Color(0xFFF81EF8)
    }
}

fun getBorderColorForStatus(orderStatus: String): Color {
    return when (orderStatus) {
        "Work In Progress" -> Color(0xFFF8751E)
        "Pickup Aligned" -> Color(0x29F81EF8)
        "Part Delivered" -> Color(0x2911CA3C)
        "Pickup Done" -> Color(0xD4D69B0C)
        "Invoice Generated" -> Color(0x291873E4)
        "Ready for Delivery" -> Color(0x29721EF8)
        else -> Color(0xD4D69B0C)
    }
}

fun getBgColorForStatus(orderStatus: String): Color {
    return when (orderStatus) {
        "Work In Progress" -> Color(0xFFFFE7D7)
        "Pickup Aligned" -> Color(0xFFFFD7F8)
        "Part Delivered" -> Color(0xFFD4FFDE)
        "Pickup Done" -> Color(0xFFFFF4D8)
        "Invoice Generated" -> Color(0xFFD2E6FF)
        "Ready for Delivery" -> Color(0xFFE9DCFF)
        else -> Color(0xFFFFD7F8)
    }
}

@Preview
@Composable
fun OrderCardPreview() {
    OrderCard(
        order = OrderData(
            orderId = "1245",
            status = "Work In Progress",
            carMake = "Hyundai i20, 2023",
            deliveryDate = "12/05/25"
        ),
        orderType = "Not Repairable"
    )
}

@Preview
@Composable
fun OrderCardFromOrderListingPreview() {
    OrderCard(
        order = OrderData(
            orderId = "1245",
            status = "Work In Progress",
            carMake = "Hyundai i20, 2023",
            deliveryDate = "12/05/25",
            dealerName = "Prem Motors",
            dealerLocation = "Sector 18, Gurgoan,\nHaryana "
        ),
        isExpanded = true,
        fromOrderListing = true,
        orderType = "Not Repairable"
    )
}