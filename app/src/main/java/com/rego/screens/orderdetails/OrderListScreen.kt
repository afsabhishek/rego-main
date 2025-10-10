package com.rego.screens.orderdetails

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.components.OrderCard
import com.rego.screens.components.OrderData
import com.rego.screens.main.home.data.LeadStatus
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontBoldPoppins
import com.rego.ui.theme.fontMediumPoppins
import com.rego.ui.theme.fontSemiBoldPoppins
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

data class PartTypeTab(
    val name: String,
    val iconRes: Int,
    val status: String
)

// Map tabs to actual lead statuses from the API
val partTypeTabs = listOf(
    PartTypeTab("Work In Progress", R.drawable.alloy_wheel, LeadStatus.WORK_IN_PROGRESS.value),
    PartTypeTab("Pickup Aligned", R.drawable.car_light, LeadStatus.PICKUP_ALIGNED.value),
    PartTypeTab("Part Delivered", R.drawable.car_bumper, LeadStatus.PART_DELIVERED.value),
    PartTypeTab("Ready for Delivery", R.drawable.car_seat, LeadStatus.READY_FOR_DELIVERY.value)
)

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    orderType: String = "Ongoing Orders",
    onBackClick: () -> Unit = {},
    onOrderClick: (String) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: OrderDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    val listState = rememberLazyListState()
    val currentTab = remember { androidx.compose.runtime.mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Load first tab data on init
        viewModel.setEvent(
            OrderDetailsEvent.LoadLeadsByStatus(
                status = partTypeTabs[0].status,
                page = 1
            )
        )
    }

    // Detect when user scrolls to bottom for pagination
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
            }
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && state.hasMorePages && !state.isLoadingMore) {
            viewModel.setEvent(OrderDetailsEvent.LoadMoreLeads)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = orderType,
                        style = fontSemiBoldPoppins().copy(fontSize = 16.sp, color = Color.White)
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable { onBackClick() },
                        tint = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color00954D
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Green header with tabs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                        color = Color00954D
                    )
            ) {
                TabRow(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    selectedTabIndex = currentTab.value,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[currentTab.value])
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)
                                ),
                            color = Color.White,
                            height = 5.dp
                        )
                    }
                ) {
                    partTypeTabs.forEachIndexed { index, partType ->
                        Tab(
                            selected = currentTab.value == index,
                            onClick = {
                                currentTab.value = index
                                coroutineScope.launch {
                                    viewModel.setEvent(
                                        OrderDetailsEvent.LoadLeadsByStatus(
                                            status = partType.status,
                                            page = 1
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.background(
                                        color = if (currentTab.value == index) Color.White else Color.White.copy(
                                            alpha = 0.13f
                                        ), shape = CircleShape
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(id = partType.iconRes),
                                        contentDescription = partType.name,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(28.dp),
                                        tint = if (currentTab.value == index) Color00954D else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    modifier = Modifier.height(26.dp),
                                    text = partType.name,
                                    style = if (currentTab.value == index) fontBoldPoppins().copy(
                                        color = Color.White
                                    ) else fontSemiBoldPoppins().copy(color = Color.White.copy(alpha = 0.8f)),
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Filter and Sort section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { /* Filter click */ }
                        .border(
                            width = 1.dp, color = Color.Black.copy(
                                alpha = 0.08f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            top = 4.dp,
                            bottom = 4.dp,
                            end = 4.dp
                        ),
                        text = "Filter",
                        style = fontMediumPoppins().copy(fontSize = 12.sp),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = "Filter",
                        modifier = Modifier.size(12.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { /* Sort click */ }
                        .border(
                            width = 1.dp, color = Color.Black.copy(
                                alpha = 0.08f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            top = 4.dp,
                            bottom = 4.dp,
                            end = 4.dp
                        ),
                        text = "Sort by",
                        style = fontMediumPoppins().copy(fontSize = 12.sp),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = "Sort",
                        modifier = Modifier.size(10.dp),
                        tint = Color00954D
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            // Content based on state
            when {
                state.progressBarState == com.rego.screens.base.ProgressBarState.Loading && state.leads.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null && state.leads.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Failed to load orders",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    viewModel.setEvent(
                                        OrderDetailsEvent.LoadLeadsByStatus(
                                            status = partTypeTabs[currentTab.value].status,
                                            page = 1
                                        )
                                    )
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {
                    OrderListContent(
                        leads = state.leads,
                        isLoadingMore = state.isLoadingMore,
                        onOrderClick = onOrderClick,
                        listState = listState
                    )
                }
            }
        }
    }
}

@Composable
fun OrderListContent(
    leads: List<LeadsResponse.LeadsData.Lead>,
    isLoadingMore: Boolean,
    onOrderClick: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Total count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                style = fontMediumPoppins().copy(fontSize = 14.sp),
                color = Color1A1A1A_40()
            )
            Text(
                text = String.format("%02d", leads.size),
                style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                color = Color.Black.copy(alpha = 0.87f)
            )
        }

        if (leads.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No orders found",
                    color = Color.Gray,
                    style = fontMediumPoppins().copy(fontSize = 14.sp)
                )
            }
        } else {
            // Order list
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leads) { lead ->
                    OrderCard(
                        order = mapLeadToOrderData(lead),
                        orderType = formatStatus(lead.status),
                        isExpanded = true,
                        onToggleExpanded = {},
                        onCardClick = { onOrderClick(lead.leadId) },
                        fromOrderListing = true
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

// Helper function to map Lead to OrderData
fun mapLeadToOrderData(lead: LeadsResponse.LeadsData.Lead): OrderData {
    return OrderData(
        orderId = lead.leadId,
        status = formatStatus(lead.status),
        carMake = "${lead.vehicle.make} ${lead.vehicle.model}, ${lead.makeYear}",
        deliveryDate = formatDate(lead.activity.lastUpdatedAt),
        dealerName = lead.dealer.name,
        dealerLocation = lead.dealer.location
    )
}

fun formatStatus(status: String): String {
    return status.replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}

fun formatDate(dateString: String?): String {
    if (dateString == null) return "TBD"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: "TBD"
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "TBD"
        } catch (e2: Exception) {
            "TBD"
        }
    }
}

@Preview
@Composable
fun PreviewOrderListScreen() {
    NativeAndroidBaseArchitectureTheme {
        OrderListScreen()
    }
}