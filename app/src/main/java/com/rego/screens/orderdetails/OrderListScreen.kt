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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontBoldPoppins
import com.rego.ui.theme.fontMediumPoppins
import com.rego.ui.theme.fontSemiBoldPoppins
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

data class PartType(
    val name: String,
    val iconRes: Int
)

// Part types with correct icons
val partTypes = listOf(
    PartType("Alloy wheels", R.drawable.alloy_wheel),
    PartType("Headlamps", R.drawable.car_light),
    PartType("Plastic repair", R.drawable.car_bumper),
    PartType("Leather & fabric repair", R.drawable.car_seat)
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
    val pagerState = rememberPagerState(pageCount = { partTypes.size })

    LaunchedEffect(Unit) {
        viewModel.setEvent(OrderDetailsEvent.Init)
    }

    var expandedCard by remember { mutableStateOf<String?>(null) }

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
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp)
                                ),
                            color = Color.White,
                            height = 5.dp
                        )
                    }
                ) {
                    partTypes.forEachIndexed { index, partType ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                    viewModel.setEvent(OrderDetailsEvent.LoadOrders(index))
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
                                        color = if (pagerState.currentPage == index) Color.White else Color.White.copy(
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
                                        tint = if (pagerState.currentPage == index) Color00954D else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    modifier = Modifier.height(26.dp),
                                    text = partType.name,
                                    style = if (pagerState.currentPage == index) fontBoldPoppins().copy(
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

            // Horizontal Pager with order lists
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val ordersForPage = state.orderListByType?.get(page) ?: emptyList()

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
                            text = String.format("%02d", ordersForPage.size),
                            style = fontSemiBoldPoppins().copy(fontSize = 14.sp),
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                    }

                    // Order list using reusable OrderCard
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ordersForPage) { order ->
                            OrderCard(
                                order = order,
                                orderType = orderType,
                                isExpanded = true,
                                onToggleExpanded = {},
                                onCardClick = { onOrderClick(order.orderId) },
                                fromOrderListing = true
                            )
                        }
                    }
                }
            }
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