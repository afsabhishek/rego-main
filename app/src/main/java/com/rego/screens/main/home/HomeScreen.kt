package com.rego.screens.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_16
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.ColorFBFBFB
import com.rego.ui.theme.fontLightPoppins
import com.rego.ui.theme.fontMediumPoppins
import com.rego.ui.theme.fontSemiBoldPoppins
import org.koin.compose.viewmodel.koinViewModel

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
    val events = homeViewModel::onTriggerEvent

    LaunchedEffect(Unit) {
        events(HomeEvent.Init)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DefaultScreenUI(
            progressBarState = state.progressBarState,
            isBottomBarInScreen = true,
            isRefreshingEnabled = true,
            onRefresh = {
                events(HomeEvent.RefreshData)
            },
            errors = errors
        ) { paddingValues ->
            TopBarSection(
                paddingValues = paddingValues,
                userName = state.userName ?: "User",
                userInitial = state.userInitial,
                onNotificationClick = onNotificationClick
            )
            HomeScreenContent(
                state = state,
                onRaiseRequest = onRaiseRequest,
                onGridOptionClick = onGridOptionClick,
                onOrderClick = onOrderClick,
                onOrderListClick = onOrderListClick,
                onNotificationClick = onNotificationClick,
                onFilterClick = { filter ->
                    events(HomeEvent.FilterLeads(filter))
                }
            )
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

@Composable
fun HomeScreenContent(
    state: HomeViewState,
    modifier: Modifier = Modifier,
    onRaiseRequest: () -> Unit,
    onGridOptionClick: () -> Unit,
    onOrderClick: () -> Unit,
    onOrderListClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFilterClick: (String?) -> Unit = {}
) {
    var expandedCard by remember { mutableStateOf<String?>(null) }

    // Use the computed property from HomeViewState
    val displayOrders = state.displayOrders

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // SEARCH BAR
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
                        style = fontMediumPoppins().copy(fontSize = 12.sp),
                        color = Color1A1A1A_40()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RAISE REQUEST CARD
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
                            style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                            color = Color1A1A1A_90()
                        )
                        Text(
                            text = "Send request to REGO CRs for part repairs",
                            style = fontMediumPoppins().copy(fontSize = 10.sp),
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

            // ONGOING ORDERS SECTION
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Summary Cards Section
                if (state.summaryCards?.isNotEmpty() == true) {
                    item {
                        // SUMMARY CARDS GRID - 2x3 grid layout (6 cards total)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                        ) {
                            // First row (2 cards)
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
                                            onClick = { onOrderListClick(label) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Second row (2 cards)
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
                                                    onClick = { onOrderListClick(label) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Third row (2 cards)
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
                                                    onClick = { onOrderListClick(label) },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(26.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray,
                        )
                    }
                }

                // Ongoing Orders Header
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
                                style = fontSemiBoldPoppins().copy(fontSize = 16.sp),
                                color = Color(0xE61A1A1A)
                            )
                            // Only show count if greater than 0
                            if (displayOrders.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${displayOrders.size})",
                                    style = fontMediumPoppins().copy(fontSize = 15.sp),
                                    color = Color(0xFFFF514F)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "View All",
                                style = fontMediumPoppins().copy(fontSize = 12.sp),
                                color = Color(0xFF00954D),
                                modifier = Modifier.clickable {
                                    onOrderListClick("Ongoing Orders")
                                }
                            )
                        }
                        Text(
                            text = "Manage all your order in one go.",
                            style = fontLightPoppins().copy(fontSize = 12.sp),
                            color = Color(0x991A1A1A),
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }
                }

                // Quick Filters
                if (displayOrders.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                            val quickFilters = state.quickFilters
                            if (quickFilters?.isNotEmpty() == true) {
                                items(quickFilters) { filter ->
                                    val selected = filter == state.selectedFilter
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .background(
                                                if (selected) Color00954D else Color.White,
                                                RoundedCornerShape(21.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color1A1A1A_16(),
                                                RoundedCornerShape(21.dp)
                                            )
                                            .clickable {
                                                onFilterClick(if (selected) null else filter)
                                            }
                                    ) {
                                        Text(
                                            text = filter,
                                            style = fontMediumPoppins().copy(fontSize = 10.sp),
                                            color = if (selected) Color.White else Color1A1A1A_60(),
                                            modifier = Modifier.padding(
                                                horizontal = 13.dp,
                                                vertical = 7.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Order Cards
                if (displayOrders.isEmpty() && state.progressBarState == ProgressBarState.Idle) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.isSearching) "Searching..." else "No orders found",
                                style = fontMediumPoppins().copy(fontSize = 14.sp),
                                color = Color1A1A1A_60()
                            )
                        }
                    }
                } else {
                    items(displayOrders) { order ->
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
                }

                // Bottom padding for navigation bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
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
                    style = fontMediumPoppins().copy(fontSize = 12.sp),
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
                    style = fontSemiBoldPoppins().copy(fontSize = 24.sp),
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
                    style = fontMediumPoppins().copy(fontSize = 12.sp)
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
                    style = fontMediumPoppins().copy(fontSize = 12.sp)
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
fun TopBarSection(
    paddingValues: PaddingValues,
    userName: String,
    userInitial: String,
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
            // Profile circle with menu icon overlay
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
                        style = fontSemiBoldPoppins().copy(fontSize = 22.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                // Menu icon overlay (positioned at bottom-end)
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
                    style = fontSemiBoldPoppins().copy(fontSize = 16.sp),
                    color = Color.White
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