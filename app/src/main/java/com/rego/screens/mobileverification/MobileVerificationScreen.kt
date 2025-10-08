package com.rego.screens.mobileverification

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.base.DefaultScreenUI
import com.rego.screens.base.EffectHandler
import com.rego.screens.base.ProgressBarState
import com.rego.screens.components.OtpInputViewType2
import com.rego.screens.components.RegoButton
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileVerificationScreen(
    onVerificationComplete: () -> Unit
) {
    // Get ViewModel from Koin DI
    val viewModel: MobileVerificationViewModel = koinViewModel()

    // Observe state
    val state by viewModel.state.collectAsState()

    // Pager state for navigation between screens
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.setEvent(MobileVerificationEvent.Init)
    }

    // Handle navigation actions from ViewModel
    EffectHandler(effectFlow = viewModel.action) { action ->
        when (action) {
            is MobileVerificationAction.NavigateToHome -> {
                onVerificationComplete()
            }
            is MobileVerificationAction.ShowOtpExpiredDialog -> {
                // Handle OTP expired if needed
            }
        }
    }

    // Navigate to OTP screen when OTP is successfully requested
    LaunchedEffect(state.isOtpRequested) {
        if (state.isOtpRequested && pagerState.currentPage == 0) {
            pagerState.animateScrollToPage(1)
        }
    }

    // Main UI with error handling
    DefaultScreenUI(
        progressBarState = state.progressBarState,
        errors = viewModel.errors
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color00954D)
        ) {
            // Logo at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rego_brandmark),
                    contentDescription = "Rego Logo",
                    tint = Color.White
                )
            }

            // Decorative box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 305.dp, start = 10.dp, end = 10.dp)
                    .height(65.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            // Main content with rounded top corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 320.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false
                ) { page ->
                    when (page) {
                        0 -> MobileNumberInputScreen(
                            mobileNumber = state.mobileNumber,
                            onMobileNumberChange = { newNumber ->
                                viewModel.setEvent(
                                    MobileVerificationEvent.MobileNumberChanged(newNumber)
                                )
                            },
                            onGetOtpClick = {
                                // TRIGGER GET OTP
                                viewModel.setEvent(MobileVerificationEvent.GetOtp)
                            },
                            isLoading = state.progressBarState == ProgressBarState.Loading,
                            errorMessage = state.errorMessage
                        )

                        1 -> OtpVerificationScreen(
                            mobileNumber = state.mobileNumber,
                            resendTimer = state.resendTimer,
                            isResendEnabled = state.isResendEnabled,
                            otp = state.otp,
                            onOtpChange = { newOtp ->
                                viewModel.setEvent(
                                    MobileVerificationEvent.OtpChanged(newOtp)
                                )
                            },
                            onResendClick = {
                                // TRIGGER RESEND OTP
                                viewModel.setEvent(MobileVerificationEvent.ResendOtp)
                            },
                            onVerifyClick = {
                                // TRIGGER VERIFY OTP
                                viewModel.setEvent(MobileVerificationEvent.VerifyOtp)
                            },
                            isLoading = state.progressBarState == ProgressBarState.Loading,
                            errorMessage = state.errorMessage,
                            resendCount = state.resendCount,
                            backendMessage = state.backendMessage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileNumberInputScreen(
    mobileNumber: String,
    onMobileNumberChange: (String) -> Unit,
    onGetOtpClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome",
            color = Color00954D,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter your phone number to continue",
            color = Color1A1A1A.copy(alpha = 0.87f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mobile number input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 1.dp,
                    color = if (errorMessage != null) Color.Red else Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+91  |",
                fontSize = 14.sp,
                color = Color1A1A1A.copy(alpha = 0.87f),
                modifier = Modifier.background(Color.Transparent),
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { value ->
                    // Allow only digits
                    if (value.all { it.isDigit() }) {
                        onMobileNumberChange(value)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Enter Mobile Number",
                        color = Color1A1A1A.copy(alpha = 0.4f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(color = Color1A1A1A.copy(alpha = 0.9f)),
                enabled = !isLoading
            )
        }

        // Error message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Instructions
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You will receive a 6-digit OTP on this number",
            color = Color1A1A1A.copy(alpha = 0.6f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Get OTP Button
        RegoButton(
            onClick = onGetOtpClick,
            text = if (isLoading) "Sending..." else "Get OTP",
            enabled = !isLoading && mobileNumber.isNotEmpty(),
            height = 44.dp
        )

        // Loading indicator
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color00954D
            )
        }

        // Test info (remove in production)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "For testing: Use OTP '111111'",
            color = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun OtpVerificationScreen(
    mobileNumber: String,
    resendTimer: Int,
    isResendEnabled: Boolean,
    otp: String,
    onOtpChange: (String) -> Unit,
    onResendClick: () -> Unit,
    onVerifyClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    resendCount: Int,
    backendMessage: String?
) {
    var localOtp by remember(otp) { mutableStateOf(otp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Verification Code",
            color = Color00954D,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Please enter the verification code sent to",
            color = Color1A1A1A.copy(alpha = 0.6f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = "+91$mobileNumber",
            color = Color1A1A1A.copy(alpha = 0.87f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        // Show backend message if available
        backendMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color00954D,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // OTP Input
        OtpInputViewType2(
            otpLength = 6,
            onOtpComplete = { completedOtp ->
                localOtp = completedOtp
                onOtpChange(completedOtp)
            },
            onCurrentOtpLength = { currentLength ->
                // Handle current OTP length if needed
            },
            onDoneClick = { completedOtp ->
                if (completedOtp.length == 6) {
                    onOtpChange(completedOtp)
                    onVerifyClick()
                }
            }
        )

        // Error message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Resend code section
        if (isResendEnabled && !isLoading) {
            TextButton(
                onClick = onResendClick,
                enabled = !isLoading
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Resend Code",
                        color = Color00954D,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (resendCount > 0) {
                        Text(
                            text = "Resent $resendCount time${if (resendCount > 1) "s" else ""}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else if (!isLoading) {
            Text(
                text = "Resend code in 0:${String.format("%02d", resendTimer)}",
                color = Color1A1A1A.copy(alpha = 0.87f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Verify Button
        RegoButton(
            onClick = {
                if (localOtp.length == 6) {
                    onOtpChange(localOtp)
                    onVerifyClick()
                }
            },
            text = if (isLoading) "Verifying..." else "Sign In",
            enabled = !isLoading && localOtp.length == 6,
            height = 44.dp
        )

        // Loading indicator
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color00954D
            )
        }

        // Test info (remove in production)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "For testing: Use OTP '111111'",
            color = Color.Gray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun MobileVerificationScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        MobileVerificationScreen(
            onVerificationComplete = {}
        )
    }
}