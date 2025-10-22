// app/src/main/java/com/rego/screens/main/home/HomeScreen.kt
package com.rego.screens.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.OrderCard
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_16
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.fontLightMontserrat
import com.rego.ui.theme.fontSemiBoldMontserrat
import org.koin.androidx.compose.koinViewModel

/**
 * Main Home Screen that shows different UI based on user role
 * CSM: Current Insurer app UI - Shows 6 cards (New Leads, Total Leads, Approved, Not Repairable, Completed, Work in Progress)
 * CR: New workshop partner UI - Shows 4 cards (Assigned Leads, In Progress, Pending, Completed)
 */
@Composable
fun HomeScreen(
    userId: String?,
    firebaseUid: String?,
    onProfileClick: () -> Unit,
    onRaiseRequest: () -> Unit,
    onGridOptionClick: () -> Unit,
    onOrderClick: () -> Unit,
    onSearchClick: () -> Unit,
    onOrderListClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val errors = homeViewModel.errors
    val state by homeViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.onTriggerEvent(HomeEvent.Init)
    }

    // Determine user role from preferences or state
    val userRole = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // In real app, fetch from UserPreferences
        // For now, we'll check from state or preferences
        // userRole.value = getUserRoleFromPreferences()
        userRole.value = "CSM" // Default to CSM, will be updated from API/preferences
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        when (userRole.value) {
            "CR" -> {
                // CR (Workshop Partner) Home
                CRHomeContent(
                    state = state,
                    onProfileClick = onProfileClick,
                    onNotificationClick = onNotificationClick,
                    onViewLeads = onOrderListClick,
                    errors = errors
                )
            }
            "CSM" -> {
                // CSM (Insurer) Home - Original UI
                CSMHomeContent(
                    state = state,
                    onProfileClick = onProfileClick,
                    onRaiseRequest = onRaiseRequest,
                    onGridOptionClick = onGridOptionClick,
                    onOrderClick = onOrderClick,
                    onOrderListClick = onOrderListClick,
                    onSearchClick = onSearchClick,
                    onNotificationClick = onNotificationClick,
                    errors = errors
                )
            }
            else -> {
                // Loading state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        BottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(),
            isHomeSelected = true,
            onHomeClick = { },
            onProfileClick = onProfileClick
        )
    }
}

// ==================== CSM HOME (Insurer - Current UI) ====================
@Composable
private fun CSMHomeContent(
    state: HomeViewState,
    onProfileClick: () -> Unit,
    onRaiseRequest: () -> Unit,
    onGridOptionClick: () -> Unit,
    onOrderClick: () -> Unit,
    onOrderListClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    errors: kotlinx.coroutines.flow.Flow<com.rego.screens.base.UIComponent>
) {
    var expandedCard by remember { mutableStateOf<String?>(null) }
    var selectedQuickFilter by remember { mutableStateOf<String?>(null) }

    val displayOrders = state.displayOrders

    val filteredOrdersForQuickFilter = if (selectedQuickFilter != null) {
        displayOrders.filter { order ->
            order.status.replace(" ", "_").uppercase() == selectedQuickFilter?.replace(" ", "_")?.uppercase()
        }
    } else {
        displayOrders
    }

    DefaultScreenUI(
        progressBarState = state.progressBarState,
        isBottomBarInScreen = true,
        isRefreshingEnabled = true,
        onRefresh = {
            // Refresh logic
        },
        errors = errors
    ) { paddingValues ->
        TopBarSection(
            paddingValues = paddingValues,
            userName = state.userName ?: "User",
            userInitial = state.userInitial,
            userRole = "CSM",
            onNotificationClick = onNotificationClick
        )

        Box(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        color = Color00954D,
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .height(48.dp)
                            .clickable { onSearchClick() },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "Search",
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF00954D)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Search claim number etc",
                                style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                color = Color1A1A1A_40()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Raise request card
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { onRaiseRequest() }
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFCAFFE5)
                                    )
                                ), RoundedCornerShape(12.dp)
                            )
                            .height(70.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.raise_request),
                                contentDescription = "Raise Request",
                                modifier = Modifier.size(52.dp),
                                tint = Color.Unspecified,
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp)
                            ) {
                                Text(
                                    text = "Raise a request",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
                                    color = Color1A1A1A_90()
                                )
                                Text(
                                    text = "Send request to REGO CRs for part repairs",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 10.sp),
                                    color = Color1A1A1A_60()
                                )
                            }
                            Icon(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Arrow",
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(180f),
                                tint = Color00954D
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Summary Cards - FIXED: Now displays all 6 cards (CSM)
                // Cards: 1-2 in Row 1, 3-4 in Row 2, 5-6 in Row 3
                if (state.summaryCards?.isNotEmpty() == true) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                        ) {
                            // Row 1: Cards 1-2 (New Leads, Total Leads)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.summaryCards?.take(2)
                                    ?.forEach { (label, iconRes, value) ->
                                        SummaryCard(
                                            label = label,
                                            iconRes = iconRes,
                                            value = value,
                                            onClick = {
                                                onOrderListClick(label)
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Row 2: Cards 3-4 (Approved, Not Repairable)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.summaryCards?.let { cards ->
                                    if (cards.size > 2) {
                                        cards.subList(2, minOf(4, cards.size))
                                            .forEach { (label, iconRes, value) ->
                                                SummaryCard(
                                                    label = label,
                                                    iconRes = iconRes,
                                                    value = value,
                                                    onClick = {
                                                        onOrderListClick(label)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Row 3: Cards 5-6 (Completed, Work in Progress) - NEWLY ADDED
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.summaryCards?.let { cards ->
                                    if (cards.size > 4) {
                                        cards.subList(4, minOf(6, cards.size))
                                            .forEach { (label, iconRes, value) ->
                                                SummaryCard(
                                                    label = label,
                                                    iconRes = iconRes,
                                                    value = value,
                                                    onClick = {
                                                        onOrderListClick(label)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                    } else if (cards.size == 5) {
                                        // If only 5 cards, show the 5th in a single card Row
                                        cards.subList(4, 5)
                                            .forEach { (label, iconRes, value) ->
                                                SummaryCard(
                                                    label = label,
                                                    iconRes = iconRes,
                                                    value = value,
                                                    onClick = {
                                                        onOrderListClick(label)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }

                // Ongoing orders
                item {
                    Column(
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Ongoing Orders",
                                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                                color = Color(0xE61A1A1A)
                            )
                            if (displayOrders.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${filteredOrdersForQuickFilter.size})",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 15.sp),
                                    color = Color(0xFFFF514F)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "View All",
                                style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                color = Color(0xFF00954D),
                                modifier = Modifier.clickable {
                                    selectedQuickFilter = null
                                    onOrderListClick("Ongoing Orders")
                                }
                            )
                        }
                        Text(
                            text = "Manage all your order in one go.",
                            style = fontLightMontserrat().copy(fontSize = 12.sp),
                            color = Color(0x991A1A1A),
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }

                // Orders list
                items(filteredOrdersForQuickFilter) { order ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OrderCard(
                            order = order,
                            orderType = "Ongoing Order",
                            isExpanded = expandedCard == order.orderId,
                            onToggleExpanded = {
                                expandedCard = if (expandedCard == order.orderId) null else order.orderId
                            },
                            onCardClick = { onOrderClick() },
                        )
                        Spacer(modifier = Modifier.height(11.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// ==================== CR HOME (Workshop Partner - New UI) ====================
@Composable
private fun CRHomeContent(
    state: HomeViewState,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onViewLeads: (String) -> Unit = {},
    errors: kotlinx.coroutines.flow.Flow<com.rego.screens.base.UIComponent>
) {
    var selectedMonth by remember { mutableStateOf("April") }

    DefaultScreenUI(
        progressBarState = state.progressBarState,
        isBottomBarInScreen = false,
        isRefreshingEnabled = true,
        onRefresh = {
            // Refresh logic
        },
        errors = errors
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ========== GREEN HEADER SECTION (Header + Order Summary) ==========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color00954D)
                        .padding(top = paddingValues.calculateTopPadding())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                    ) {
                        // HEADER ROW
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.size(42.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.24f),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .background(Color.Transparent, shape = RoundedCornerShape(100.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.userInitial,
                                        style = fontSemiBoldMontserrat().copy(fontSize = 22.sp),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(20.dp)
                                        .background(Color.White, shape = RoundedCornerShape(100.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.menu),
                                        contentDescription = "Menu",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome ${state.userName?.split(" ")?.firstOrNull() ?: "CR"},",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                                    color = Color.White
                                )
                                Text(
                                    text = "Workshop Partner",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                painter = painterResource(R.drawable.notification),
                                contentDescription = "Notification",
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onNotificationClick() },
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // SEARCH BOX (Between Header and Order Summary)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .height(48.dp)
                                .clickable { onViewLeads("Search") },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "Search",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF00954D)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Search orders",
                                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                    color = Color1A1A1A_40()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ORDER SUMMARY ROW
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Order Summary",
                                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            // Month Filter Dropdown
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable { /* Handle month selection */ }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = selectedMonth,
                                        style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                        color = Color.White
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.back),
                                        contentDescription = "Dropdown",
                                        modifier = Modifier
                                            .size(12.dp)
                                            .rotate(270f),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ========== GREEN CARDS CONTAINER (Separate rounded box) ==========
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(Color00954D, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Row 1: New Orders, Not Repairable
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CROrderCard(
                                icon = R.drawable.total_leads,
                                label = "New Orders",
                                value = state.leadStatsItems?.find { it.label == "Total Leads" }?.count?.toString() ?: "0",
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                onClick = { onViewLeads("New Orders") }
                            )
                            CROrderCard(
                                icon = R.drawable.pending,
                                label = "Not Repairable",
                                value = state.leadStatsItems?.find { it.label == "Not Repairable" }?.count?.toString() ?: "0",
                                color = Color(0xFFFF9800),
                                modifier = Modifier.weight(1f),
                                onClick = { onViewLeads("Not Repairable") }
                            )
                        }

                        // Row 2: Ongoing Orders, Completed
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CROrderCard(
                                icon = R.drawable.audience,
                                label = "Ongoing Orders",
                                value = state.displayOrders.size.toString(),
                                color = Color(0xFFFFC107),
                                modifier = Modifier.weight(1f),
                                onClick = { onViewLeads("Ongoing Orders") }
                            )
                            CROrderCard(
                                icon = R.drawable.completed,
                                label = "Completed",
                                value = state.leadStatsItems?.find { it.label == "Completed" }?.count?.toString() ?: "0",
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f),
                                onClick = { onViewLeads("Completed") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Spacer(modifier = Modifier.height(20.dp))

                // Ongoing Orders Section
                if (state.displayOrders.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Ongoing Orders",
                                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                                color = Color1A1A1A_90()
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "View All",
                                style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                                color = Color00954D,
                                modifier = Modifier.clickable {
                                    onViewLeads("Ongoing Orders")
                                }
                            )
                        }
                        Text(
                            text = "Manage all your order in one go.",
                            style = fontLightMontserrat().copy(fontSize = 12.sp),
                            color = Color1A1A1A_60(),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Order cards list
                    state.displayOrders.take(3).forEach { order ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            OrderCard(
                                order = order,
                                orderType = "Ongoing Order",
                                isExpanded = false,
                                onToggleExpanded = { },
                                onCardClick = { onViewLeads(order.orderId) },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ongoing Orders",
                            style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                            color = Color1A1A1A_90(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Manage all your order in one go.",
                            style = fontLightMontserrat().copy(fontSize = 12.sp),
                            color = Color1A1A1A_60(),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        Text(
                            text = "No ongoing orders yet",
                            style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
                            color = Color1A1A1A_60(),
                            modifier = Modifier.padding(vertical = 40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// ==================== CR Order Card Component ====================
@Composable
private fun CROrderCard(
    icon: Int,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = label,
                style = fontSemiBoldMontserrat().copy(fontSize = 11.sp),
                color = Color1A1A1A_60(),
                textAlign = TextAlign.Center
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = value,
                    style = fontSemiBoldMontserrat().copy(fontSize = 22.sp),
                    color = Color.Black
                )
            }

            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(12.dp)
                    .rotate(180f),
                tint = color
            )
        }
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    isHomeSelected: Boolean = false,
    isProfileSelected: Boolean = false,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = {
                if (!isHomeSelected) {
                    onHomeClick()
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "Home",
                    tint = if (isHomeSelected) Color00954D else Color.Gray
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = if (isHomeSelected) Color00954D else Color.Gray,
                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color00954D,
                selectedTextColor = Color00954D,
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = isProfileSelected,
            onClick = {
                if (!isProfileSelected) {
                    onProfileClick()
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "Account",
                    tint = if (isProfileSelected) Color00954D else Color.Gray
                )
            },
            label = {
                Text(
                    text = "Account",
                    color = if (isProfileSelected) Color00954D else Color.Gray,
                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color00954D,
                selectedTextColor = Color00954D,
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

@Composable
private fun TopBarSection(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    userName: String,
    userInitial: String,
    userRole: String,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color00954D)
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.size(42.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.24f),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .background(Color.Transparent, shape = RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInitial,
                        style = fontSemiBoldMontserrat().copy(fontSize = 22.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .background(Color.White, shape = RoundedCornerShape(100.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Menu",
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome ${userName.split(" ").firstOrNull() ?: userName},",
                    style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                    color = Color.White
                )
                Text(
                    text = if (userRole == "CR") "Workshop Partner" else "Insurance Partner",
                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Icon(
                painter = painterResource(R.drawable.notification),
                contentDescription = "Notification",
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onNotificationClick() },
                tint = Color.White
            )
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    iconRes: Int,
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(94.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                    color = Color1A1A1A_60()
                )
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$value",
                    style = fontSemiBoldMontserrat().copy(fontSize = 24.sp),
                    color = Color.Black
                )
                Spacer(Modifier.width(14.dp))
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .size(13.dp)
                        .rotate(180f),
                    tint = Color00954D
                )
            }
        }
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "WORK_IN_PROGRESS", "WORK IN PROGRESS" -> Color(0xFFF8751E)
        "PICKUP_ALIGNED", "PICKUP ALIGNED" -> Color(0xFFF81EF8)
        "PART_DELIVERED", "PART DELIVERED" -> Color(0xFF11CA3C)
        "PICKUP_DONE", "PICKUP DONE" -> Color(0xFFD69B0C)
        "INVOICE_GENERATED", "INVOICE GENERATED" -> Color(0xFF1873E4)
        "READY_FOR_DELIVERY", "READY FOR DELIVERY" -> Color(0xFF721EF8)
        "NEW" -> Color(0xFF1976D2)
        "COMPLETED" -> Color(0xFF4CAF50)
        "REJECTED", "NOT REPAIRABLE" -> Color(0xFFE7503D)
        else -> Color(0xFF9E9E9E)
    }
}

@Composable
private fun CRLeadCard(
    leadId: String,
    status: String,
    partType: String,
    vehicle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lead #$leadId",
                        style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
                        color = Color1A1A1A_90()
                    )
                    Text(
                        text = vehicle,
                        style = fontSemiBoldMontserrat().copy(fontSize = 11.sp),
                        color = Color1A1A1A_60()
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = getStatusColor(status).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = status,
                        style = fontSemiBoldMontserrat().copy(fontSize = 9.sp),
                        color = getStatusColor(status)
                    )
                }
            }

            Text(
                text = "Part: $partType",
                style = fontSemiBoldMontserrat().copy(fontSize = 10.sp),
                color = Color1A1A1A_60()
            )
        }
    }
}