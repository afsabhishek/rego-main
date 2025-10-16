package com.rego.screens.raiserequest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.components.RegoButton
import com.rego.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RequestSubmittedScreen(
    leadId: String? = null,
    status: String? = null,
    createdAt: String? = null,
    onOkayClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick),
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Request Submitted Successfully!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color1A1A1A,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Your request has been submitted successfully. Our REGO CR team will review and process your request shortly.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color1A1A1A_60(),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Lead Details Card (if available)
        if (!leadId.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ColorF9F9F9),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Request Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color1A1A1A_90(),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Lead ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lead ID",
                            fontSize = 12.sp,
                            color = Color1A1A1A_60()
                        )
                        Text(
                            text = leadId,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color1A1A1A_90()
                        )
                    }

                    if (!status.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Status",
                                fontSize = 12.sp,
                                color = Color1A1A1A_60()
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = getStatusBackgroundColor(status),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = formatStatus(status),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = getStatusTextColor(status)
                                )
                            }
                        }
                    }

                    // Created Date
                    if (!createdAt.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Submitted On",
                                fontSize = 12.sp,
                                color = Color1A1A1A_60()
                            )
                            Text(
                                text = formatDate(createdAt),
                                fontSize = 12.sp,
                                color = Color1A1A1A_90()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View Details Button (optional)
            TextButton(
                onClick = onViewDetailsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View Order Details",
                    color = Color00954D,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Next Steps Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8F4)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "What happens next?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color00954D,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                BulletPoint("Your request will be reviewed by REGO CR")
                BulletPoint("You'll receive approval/disapproval notification")
                BulletPoint("Track your request status in the app")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Okay Button
        RegoButton(
            onClick = onOkayClick,
            text = "Back to Home"
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "•",
            fontSize = 12.sp,
            color = Color1A1A1A_60(),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color1A1A1A_60(),
            lineHeight = 16.sp
        )
    }
}

private fun getStatusBackgroundColor(status: String): Color {
    return when (status.uppercase()) {
        "NEW" -> Color(0xFFE3F2FD)
        "APPROVED" -> Color(0xFFE8F5E9)
        "REJECTED" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }
}

private fun getStatusTextColor(status: String): Color {
    return when (status.uppercase()) {
        "NEW" -> Color(0xFF1976D2)
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFF44336)
        else -> Color1A1A1A_60()
    }
}

private fun formatStatus(status: String): String {
    return when (status.uppercase()) {
        "NEW" -> "New Request"
        "APPROVED" -> "Approved"
        "REJECTED" -> "Rejected"
        else -> status.replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun RequestSubmittedScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        RequestSubmittedScreen(
            leadId = "LEAD172345678901ABCDE",
            status = "NEW",
            createdAt = "2024-01-01T00:00:00.000Z"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RequestSubmittedScreenSimplePreview() {
    NativeAndroidBaseArchitectureTheme {
        RequestSubmittedScreen()
    }
}