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
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import com.rego.ui.theme.fontSemiBoldMontserrat
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaiseRequestParentScreen(
    onBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    fun onSubmitNavigateToConfirmation() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(1)
        }
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
                tint = Color1A1A1A_90(),
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Raise a Request",
                style = fontSemiBoldMontserrat().copy(fontSize = 16.sp),
                color = Color1A1A1A_90(),
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
            userScrollEnabled = false // Disable manual scrolling
        ) { page ->
            when (page) {
                0 -> {
                    // Remove topBar from RaiseRequestScreen since parent has it
                    RaiseRequestScreen(
                        onSubmit = { onSubmitNavigateToConfirmation() }
                    )
                }

                1 -> {
                    RequestSubmittedScreen(
                        onOkayClick = {
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
    NativeAndroidBaseArchitectureTheme {
        RaiseRequestParentScreen()
    }
}