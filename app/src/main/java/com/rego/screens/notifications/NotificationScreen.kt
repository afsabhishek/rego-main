package com.rego.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.ColorFBFBFB
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontRegularMontserrat
import com.rego.ui.theme.fontSemiBoldMontserrat
import org.koin.androidx.compose.koinViewModel

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionText: String? = null,
    val notificationType: NotificationType = NotificationType.INFO
)

enum class NotificationType {
    INFO, SUCCESS, WARNING, ERROR, ORDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {}
) {
    val viewModel: NotificationViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setEvent(NotificationEvent.Init)
    }
    DefaultScreenUI { paddingValues ->
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
                tint = Color1A1A1A.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Notifications",
                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                color = Color.Black
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.5f))
                .height(2.dp)
                .shadow(1.dp)
        )
        Column(modifier = Modifier.background(ColorFBFBFB)) {
            NotificationScreenContent(
                notifications = state.notifications ?: emptyList(),
                onNotificationClick = { notification ->
                    viewModel.setEvent(NotificationEvent.NotificationClicked(notification))
                    onNotificationClick(notification)
                },
                isLoading = state.progressBarState == com.rego.screens.base.ProgressBarState.Loading
            )
        }
    }
}

@Composable
fun NotificationScreenContent(
    notifications: List<NotificationItem>,
    modifier: Modifier = Modifier,
    onNotificationClick: (NotificationItem) -> Unit = {},
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading...", color = Color.Gray, textAlign = TextAlign.Center)
        }
        return
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val backgroundColor = if (notification.isRead) Color.White else Color.White
    val borderColor = when (notification.notificationType) {
        NotificationType.ORDER -> Color00954D
        NotificationType.SUCCESS -> Color00954D
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> Color(0xFFE7503D)
        NotificationType.INFO -> Color.Gray.copy(alpha = 0.3f)
    }

    val indicatorColor = when (notification.notificationType) {
        NotificationType.ORDER -> Color00954D
        NotificationType.SUCCESS -> Color00954D
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> Color(0xFFE7503D)
        NotificationType.INFO -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (!notification.isRead) 1.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            // Unread indicator dot
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(indicatorColor, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = fontSemiBoldMontserrat().copy(
                            fontSize = 14.sp,
                            color = Color1A1A1A_90()
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message,
                    style = fontRegularMontserrat().copy(
                        fontSize = 12.sp,
                        color = Color1A1A1A_60()
                    ),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.timestamp,
                        style = fontRegularMontserrat().copy(
                            fontSize = 11.sp,
                            color = Color1A1A1A_60()
                        ),
                    )

                    notification.actionText?.let { actionText ->
                        Text(
                            text = actionText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = indicatorColor,
                            modifier = Modifier.clickable { /* Handle action click */ }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        NotificationScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationCardPreview() {
    NativeAndroidBaseArchitectureTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NotificationCard(
                notification = NotificationItem(
                    id = "1",
                    title = "New Order Request",
                    message = "You have a new order request for Alloy Wheel repair of Hyundai Creta, 2023",
                    timestamp = "2 hours ago",
                    isRead = false,
                    actionText = "View Order >>",
                    notificationType = NotificationType.ORDER
                ),
                onClick = {}
            )

            NotificationCard(
                notification = NotificationItem(
                    id = "2",
                    title = "Order Completed",
                    message = "Your order for Headlamp replacement has been completed successfully",
                    timestamp = "5 hours ago",
                    isRead = true,
                    actionText = "View Details",
                    notificationType = NotificationType.SUCCESS
                ),
                onClick = {}
            )
        }
    }
}