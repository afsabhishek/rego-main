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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.components.OrderCard
import com.rego.screens.components.OrderData
import com.rego.screens.main.home.data.LeadsResponse
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.fontBoldPoppins
import com.rego.ui.theme.fontSemiBoldMontserrat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

data class PartTypeTab(
    val name: String,
    val iconRes: Int,
    val partType: String
)

// ✅ Map tabs to actual PART TYPES
val partTypeTabs = listOf(
    PartTypeTab("Alloy Wheels", R.drawable.alloy_wheel, "ALLOY_WHEELS"),
    PartTypeTab("Headlamps", R.drawable.car_light, "HEADLAMPS"),
    PartTypeTab("Plastic Repair", R.drawable.car_bumper, "PLASTIC"),
    PartTypeTab("Leather & Fabric", R.drawable.car_seat, "LEATHER_FABRIC")
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

    // ✅ Determine which status array to load based on orderType
    val initialStatus = mapOrderTypeToStatuses(orderType)

    // ✅ Track if we should show tabs (for specific order types)
    val showTabs = when (orderType) {
        "Ongoing Orders", "Total Leads" -> true
        else -> true  // Show tabs for all card-based filters
    }

    LaunchedEffect(Unit) {
        println("📥 OrderListScreen loading for: $orderType")
        println("📥 Status array: $initialStatus")
        println("📥 Show tabs: $showTabs")

        // Load initial data with the status from card
        viewModel.setEvent(
            OrderDetailsEvent.LoadLeadsByStatus(
                status = initialStatus.ifEmpty { null },
                page = 1
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = orderType,
                        style = fontSemiBoldMontserrat().copy(fontSize = 16.sp, color = Color.White)
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
            // ✅ Show tabs ALWAYS for part type filtering
            if (showTabs) {
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
                                        println("🔄 Tab clicked: ${partType.name}")
                                        println("🔄 Loading with status: $initialStatus and partType: ${partType.partType}")

                                        // ✅ Load leads with BOTH status and part type
                                        viewModel.loadLeadsByStatusWithPartType(
                                            status = initialStatus.ifEmpty { null },
                                            partType = partType.partType
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
                                        ) else fontSemiBoldMontserrat().copy(color = Color.White.copy(alpha = 0.8f)),
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp
                                    )
                                }
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
                        style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
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
                        style = fontSemiBoldMontserrat().copy(fontSize = 12.sp),
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
                state.error != null && state.leads.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Failed to load orders",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    viewModel.setEvent(
                                        OrderDetailsEvent.LoadLeadsByStatus(
                                            status = if (initialStatus.isEmpty()) null else initialStatus,
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
                        onOrderClick = onOrderClick
                    )
                }
            }
        }
    }
}

@Composable
fun OrderListContent(
    leads: List<LeadsResponse.LeadsData.Lead>,
    onOrderClick: (String) -> Unit
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
                style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
                color = Color1A1A1A_40()
            )
            Text(
                text = String.format("%02d", leads.size),
                style = fontSemiBoldMontserrat().copy(fontSize = 14.sp),
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
                    style = fontSemiBoldMontserrat().copy(fontSize = 14.sp)
                )
            }
        } else {
            // Order list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leads) { lead ->
                    OrderCard(
                        order = mapLeadToOrderData(lead),
                        orderType = lead.partType,  // ✅ Use actual part type from lead
                        isExpanded = true,
                        onToggleExpanded = {},
                        onCardClick = { onOrderClick(lead.id) },
                        fromOrderListing = true
                    )
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
        dealerName = lead.dealer?.name ?: "N/A",
        dealerLocation = lead.dealer?.location ?: "N/A"
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

// ✅ Helper function to map orderType (card label) to status array
fun mapOrderTypeToStatuses(orderType: String): List<String> {
    return when (orderType) {
        "New Leads" -> listOf("NEW")
        "Total Leads" -> emptyList()  // Empty = all statuses
        "Approved" -> listOf(
            "PICKUP_ALIGNED",
            "PICKUP_DONE",
            "WORK_IN_PROGRESS",
            "READY_FOR_DELIVERY",
            "INVOICE_GENERATED",
            "PHYSICAL_INSPECTION_ALIGNED",
            "DELIVERED"
        )
        "Not Repairable" -> listOf("REJECTED")
        "Completed" -> listOf("DELIVERED")
        "Work in Progress" -> listOf(
            "PICKUP_ALIGNED",
            "PHYSICAL_INSPECTION_ALIGNED",
            "PICKUP_DONE",
            "WORK_IN_PROGRESS",
            "READY_FOR_DELIVERY",
            "INVOICE_GENERATED"
        )
        "Work In Progress" -> listOf(
            "PICKUP_ALIGNED",
            "PHYSICAL_INSPECTION_ALIGNED",
            "PICKUP_DONE",
            "WORK_IN_PROGRESS",
            "READY_FOR_DELIVERY",
            "INVOICE_GENERATED"
        )
        "Pickup Aligned" -> listOf("PICKUP_ALIGNED")
        "Part Delivered" -> listOf("PART_DELIVERED")
        "Pickup Done" -> listOf("PICKUP_DONE")
        "Invoice Generated" -> listOf("INVOICE_GENERATED")
        "Ready for Delivery" -> listOf("READY_FOR_DELIVERY")
        else -> emptyList()
    }
}