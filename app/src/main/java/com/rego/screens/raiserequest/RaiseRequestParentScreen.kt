// app/src/main/java/com/rego/screens/raiserequest/RaiseRequestParentScreen.kt
package com.rego.screens.raiserequest

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.base.EffectHandler
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.RegoTheme
import com.rego.ui.theme.fontSemiBoldMontserrat
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaiseRequestParentScreen(
    onBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val viewModel: RaiseRequestViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    // Initialize the form data on first load
    LaunchedEffect(Unit) {
        viewModel.setEvent(RaiseRequestEvent.Init)
    }

    // Handle navigation actions from ViewModel
    EffectHandler(effectFlow = viewModel.action) { action ->
        when (action) {
            is RaiseRequestAction.NavigateToSuccess -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(1)
                }
            }
            is RaiseRequestAction.ShowError -> {
                // Error handling is done through the error flow
            }
        }
    }

    DefaultScreenUI(
        progressBarState = state.progressBarState,
        errors = viewModel.errors
    ) { paddingValues ->
        Spacer(modifier = Modifier.size(paddingValues.calculateTopPadding()))

        // Header with back button and title
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
                    .clickable {
                        if (pagerState.currentPage == 1) {
                            // If on success page, go back to form
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        } else {
                            // If on form page, exit the screen
                            onBack()
                        }
                    }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (pagerState.currentPage == 0) "Raise a Request" else "Request Status",
                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                color = Color1A1A1A_90(),
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Disable manual scrolling
        ) { page ->
            when (page) {
                0 -> {
                    // Main form screen
                    RaiseRequestScreenContent(
                        state = state,
                        onFieldChange = { field, value ->
                            viewModel.setEvent(RaiseRequestEvent.FieldChanged(field, value))
                        },
                        onPartTypeSelect = { partType ->
                            viewModel.setEvent(RaiseRequestEvent.SelectPartType(partType))
                        },
                        onVehicleVariantSelect = { variant ->
                            viewModel.setEvent(RaiseRequestEvent.SelectVehicleVariant(variant))
                        },
                        onWorkshopDealerSelect = { dealer ->
                            viewModel.setEvent(RaiseRequestEvent.SelectWorkshopDealer(dealer))
                        },
                        onSubmit = {
                            viewModel.setEvent(RaiseRequestEvent.SubmitRequest)
                        }
                    )
                }

                1 -> {
                    // Success screen with lead details
                    RequestSubmittedScreen(
                        leadId = state.submitResult?.leadId,
                        status = state.submitResult?.status,
                        createdAt = state.submitResult?.createdAt,
                        onOkayClick = {
                            onBack() // Navigate back to home or previous screen
                        },
                        onViewDetailsClick = {
                            // Optional: Navigate to order details screen
                            // You can pass the leadId to navigate to order details
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaiseRequestParentScreenPreview() {
    RegoTheme {
        RaiseRequestParentScreen()
    }
}