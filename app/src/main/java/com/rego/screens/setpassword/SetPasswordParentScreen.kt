package com.rego.screens.setpassword

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.components.RegoButton
import com.rego.screens.components.TransparentInputField
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.fontSemiBoldMontserrat
import com.rego.ui.theme.fontSemiBoldPoppins
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState
import com.rego.screens.base.DefaultScreenUI
import com.rego.ui.theme.ColorE7503D

@Composable
fun SetPasswordParentScreen(
    onLoginClick: () -> Unit = {},
    userId: String = "ICI01"
) {
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    val viewModel: SetPasswordViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DefaultScreenUI(progressBarState = state.progressBarState) { paddingValues ->
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
                            0 -> WelcomeScreen(
                                userId = userId,
                                onSetPassword = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                },
                                onSignInClick = {}
                            )

                            1 -> SetPasswordScreen(
                                userId = userId,
                                state = state,
                                onPasswordChange = {
                                    viewModel.setEvent(
                                        SetPasswordEvent.PasswordChanged(
                                            it
                                        )
                                    )
                                },
                                onConfirmPasswordChange = {
                                    viewModel.setEvent(
                                        SetPasswordEvent.ConfirmPasswordChanged(
                                            it
                                        )
                                    )
                                },
                                onPasswordSet = {
                                    viewModel.setEvent(
                                        SetPasswordEvent.SetPassword(userId, state.password)
                                    )
                                },
                                isSubmitting = state.progressBarState == com.rego.screens.base.ProgressBarState.Loading,
                                passwordSetSuccess = state.isPasswordSet == true,
                                error = state.error,
                                onSuccess = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            2
                                        )
                                    }
                                }
                            )

                            2 -> PasswordSetScreen(onLogin = onLoginClick)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetPasswordParentScreenPreview() {
    SetPasswordParentScreen()
}

@Composable
private fun WelcomeScreen(
    userId: String,
    onSetPassword: () -> Unit,
    onSignInClick: () -> Unit,
) {
    var userIdValue by remember { mutableStateOf(userId) }
    var passwordValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Welcome",
            style = fontSemiBoldPoppins().copy(fontSize = 24.sp, color = Color00954D),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User ID Field with Set Password link
            TransparentInputField(
                label = "User ID",
                value = userIdValue,
                onValueChange = { userIdValue = it },
                placeholder = "Enter User ID",
                labelComposable = {
                    Text(
                        text = "User ID",
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 14.sp,
                            color = Color1A1A1A_60()
                        ),
                    )
                }
            )

            // Password Field (disabled)
            TransparentInputField(
                label = "Password",
                labelComposable = {
                    Text(
                        text = "Password",
                        style = fontSemiBoldPoppins().copy(
                            fontSize = 14.sp,
                            color = Color1A1A1A_60()
                        ),
                    )
                },
                value = passwordValue,
                onValueChange = { /* Disabled */ },
                placeholder = "Enter Password",
            )
            Text(
                text = "Set Password",
                style = fontSemiBoldPoppins().copy(
                    fontSize = 12.sp,
                    color = Color00954D
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetPassword() },
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        RegoButton(
            onClick = { onSignInClick() },
            text = "Sign In",
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(userId = "ICI01", onSetPassword = {}, onSignInClick = {})
}

@Composable
private fun SetPasswordScreen(
    userId: String,
    state: SetPasswordViewState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordSet: () -> Unit,
    isSubmitting: Boolean,
    passwordSetSuccess: Boolean,
    error: String?,
    onSuccess: () -> Unit
) {
    val password = state.password
    val confirmPassword = state.confirmPassword
    val passwordsMatch =
        password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
    val isFormValid = password.isNotEmpty() && confirmPassword.isNotEmpty() && passwordsMatch
    val showError = confirmPassword.isNotEmpty() && password != confirmPassword
    val scrollState = rememberScrollState()

    // Add local state for visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Navigate to success page if password was set
    if (passwordSetSuccess) {
        onSuccess()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Set Password",
            style = fontSemiBoldPoppins().copy(fontSize = 24.sp, color = Color00954D),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PasswordTransparentInputField(
                label = "Password",
                value = password,
                onValueChange = onPasswordChange,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                placeholder = "•••••••••",
                isError = false
            )
            PasswordTransparentInputField(
                label = "Re enter password",
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                placeholder = "•••••••••",
                isError = showError
            )
            if (showError) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.error),
                        contentDescription = "Error",
                        tint = ColorE7503D,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Password doesn't match",
                        color = ColorE7503D,
                        fontSize = 14.sp
                    )
                }
            }
            if (error != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        RegoButton(
            onClick = onPasswordSet,
            text = "Set Password",
            enabled = isFormValid && !isSubmitting
        )
    }
}

@Composable
fun PasswordTransparentInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isError: Boolean = false
) {
    Column(modifier) {
        Text(
            text = label,
            style = fontSemiBoldPoppins().copy(
                fontSize = 14.sp,
                color = Color1A1A1A_60()
            ),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        BasicTextField(
            value = value,
            modifier = Modifier.height(40.dp),
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 14.sp, color = Color1A1A1A),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = if (isError) Color.Red else Color.Black.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Color1A1A1A_40(),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                        IconButton(
                            onClick = onPasswordVisibilityChange,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.password_view),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color1A1A1A_60(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun PasswordSetScreen(
    onLogin: () -> Unit
) {
    // Bottom white section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 60.dp)
                .size(88.dp)
                .background(Color00954D, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick),
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Password Set",
            style = fontSemiBoldMontserrat().copy(
                fontSize = 16.sp,
                color = Color1A1A1A
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "You can now login with your new\npassword.",
            style = fontSemiBoldMontserrat().copy(
                fontSize = 14.sp,
                color = Color1A1A1A_60()
            ),
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        RegoButton(
            onClick = onLogin,
            text = "Login"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetPasswordScreenPreview() {
    SetPasswordScreen(
        userId = "ICI01",
        state = SetPasswordViewState(),
        onPasswordChange = { },
        onConfirmPasswordChange = { },
        onPasswordSet = { },
        isSubmitting = false,
        passwordSetSuccess = false,
        error = null,
        onSuccess = { }
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordSetScreenPreview() {
    PasswordSetScreen(onLogin = {})
}