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
 * CSM: Current Insurer app UI
 * CR: New workshop partner UI
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

                // Summary Cards
                if (state.summaryCards?.isNotEmpty() == true) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                        ) {
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
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // CR Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color00954D)
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
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Cards for CR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Total Leads, In Progress
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CRStatCard(
                            icon = R.drawable.total_leads,
                            label = "Assigned Leads",
                            value = state.leadStatsItems?.find { it.label == "Total Leads" }?.count?.toString() ?: "0",
                            color = Color00954D,
                            modifier = Modifier.weight(1f),
                            onClick = { onViewLeads("Total Leads") }
                        )
                        CRStatCard(
                            icon = R.drawable.pending,
                            label = "In Progress",
                            value = state.leadStatsItems?.find { it.label == "Work in Progress" }?.count?.toString() ?: "0",
                            color = Color(0xFFF8751E),
                            modifier = Modifier.weight(1f),
                            onClick = { onViewLeads("Work in Progress") }
                        )
                    }

                    // Row 2: Pending, Completed
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CRStatCard(
                            icon = R.drawable.audience,
                            label = "Pending",
                            value = state.leadStatsItems?.find { it.label == "New Leads" }?.count?.toString() ?: "0",
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f),
                            onClick = { onViewLeads("New Leads") }
                        )
                        CRStatCard(
                            icon = R.drawable.completed,
                            label = "Completed",
                            value = state.leadStatsItems?.find { it.label == "Completed" }?.count?.toString() ?: "0",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f),
                            onClick = { onViewLeads("Completed") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions for CR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
                        color = Color1A1A1A_90()
                    )

                    CRActionButton(
                        icon = R.drawable.raise_request,
                        title = "View Assigned Leads",
                        description = "Check all leads assigned to your workshop",
                        onClick = { onViewLeads("Assigned") }
                    )

                    CRActionButton(
                        icon = R.drawable.total_leads,
                        title = "Update Lead Status",
                        description = "Update work progress and completion",
                        onClick = { onViewLeads("All") }
                    )

                    CRActionButton(
                        icon = R.drawable.person,
                        title = "My Profile",
                        description = "View workshop details and settings",
                        onClick = onProfileClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Leads for CR
                if (state.leads.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Recent Leads",
                            style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
                            color = Color1A1A1A_90()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        state.leads.take(3).forEach { lead ->
                            CRLeadCard(
                                leadId = lead.leadId,
                                status = lead.status,
                                partType = lead.partType,
                                vehicle = "${lead.vehicle.make} ${lead.vehicle.model}",
                                onClick = { onViewLeads(lead.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==================== CR Home Components ====================

@Composable
private fun CRStatCard(
    icon: Int,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = fontSemiBoldMontserrat().copy(fontSize = 20.sp),
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = fontSemiBoldMontserrat().copy(fontSize = 10.sp),
                color = Color1A1A1A_60(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CRActionButton(
    icon: Int,
    title: String,
    description: String,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color00954D.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = fontSemiBoldMontserrat().copy(fontSize = 13.sp),
                    color = Color1A1A1A_90()
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = fontSemiBoldMontserrat().copy(fontSize = 11.sp),
                    color = Color1A1A1A_60()
                )
            }

            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Navigate",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f),
                tint = Color00954D
            )
        }
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