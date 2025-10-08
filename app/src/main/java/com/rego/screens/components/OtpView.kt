package com.rego.screens.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme

@Composable
fun OtpView(
    modifier: Modifier = Modifier,
    otpCount: Int = 4,
    onOtpChange: (String) -> Unit
) {
    val otp = remember { mutableStateListOf("1", "2", "3", "4") }
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }

    Row(modifier = modifier) {
        repeat(otpCount) { index ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 1.dp,
                        color = if (otp[index].isNotEmpty()) MaterialTheme.colorScheme.primary else Color.Gray,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = otp[index],
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            otp[index] = newValue
                            onOtpChange(otp.joinToString(""))

                            // Auto-focus next field if digit entered
                            if (newValue.isNotEmpty() && index < otpCount - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .size(48.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (otp[index].isEmpty()) {
                            Text(
                                text = "○",
                                color = Color.LightGray,
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }
            if (index < otpCount - 1) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun OtpInputView(
    otpLength: Int = 4,
    onOtpEntered: (String) -> Unit
) {
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        otpValues.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { new ->
                    if (new.length <= 1 && new.all { it.isDigit() }) {
                        otpValues[index] = new
                        if (new.isNotEmpty() && index < otpLength - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                        if (otpValues.all { it.length == 1 }) {
                            onOtpEntered(otpValues.joinToString(""))
                        }
                    }
                },
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
                    .focusRequester(focusRequesters[index])
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                            if (otpValues[index].isEmpty() && index > 0) {
                                otpValues[index - 1] = ""
                                focusRequesters[index - 1].requestFocus()
                            }
                        }
                        false
                    },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }

    // Auto focus first field when Composable is loaded
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}

@Composable
fun OtpInputViewType2(
    modifier: Modifier = Modifier,
    otpLength: Int = 4,
    onOtpComplete: (String) -> Unit,
    onCurrentOtpLength: (Int) -> Unit,
    onDoneClick: (String) -> Unit,
) {
    // 1) Hold each OTP digit as a single‐char String
    val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
    // 2) One FocusRequester per cell
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12f.toWidthPx()),
        modifier = modifier
            .wrapContentWidth()
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(otpLength) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(40f.toWidthPx())
                    .height(40f.toHeightPx())
                    .border(
                        width = 1.dp,
                        color = if (otpValues[index].isNotEmpty()) Color00954D else Color.Black.copy(
                            alpha = 0.2f
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                BasicTextField(
                    value = otpValues[index],
                    onValueChange = { newValue ->
                        // Only allow a single digit, all‐digits
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            otpValues[index] = newValue
                            // If user typed something, advance focus
                            if (newValue.isNotEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }

                            // If all boxes are filled, notify completion
                            if (otpValues.all { it.isNotEmpty() }) {
                                onOtpComplete(otpValues.joinToString(""))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if (index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        },
                        onDone = {
                            if (index == otpLength - 1) {
                                onDoneClick(otpValues.joinToString(""))
                            }
                        }
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color1A1A1A.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .focusRequester(focusRequesters[index])
                        .fillMaxWidth()
                        // 3) Intercept Backspace BEFORE BasicTextField processes it
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown &&
                                keyEvent.key == Key.Backspace
                            ) {
                                when {
                                    // If this box already empty, move focus back one and clear that
                                    otpValues[index].isEmpty() && index > 0 -> {
                                        otpValues[index - 1] = ""
                                        focusRequesters[index - 1].requestFocus()
                                        onCurrentOtpLength(otpValues.joinToString("").length)
                                        true
                                    }
                                    // If this box has a digit, just clear it
                                    otpValues[index].isNotEmpty() -> {
                                        otpValues[index] = ""
                                        onCurrentOtpLength(otpValues.joinToString("").length)
                                        true
                                    }

                                    else -> false
                                }
                            } else {
                                onCurrentOtpLength(otpValues.joinToString("").length)
                                // Let all other keys be handled normally
                                false
                            }
                        }
                )

//                Spacer(modifier = Modifier.height(4.dp))
//                Box(
//                    Modifier
//                        .size(40.dp)
//                        .background(color = Color.Green)
//                )
            }
        }
    }

    // 4) Initially request focus on the first digit
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}

@Preview
@Composable
fun OtpViewType1Preview() {
    NativeAndroidBaseArchitectureTheme {
        OtpView { }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpViewType2Preview() {
    NativeAndroidBaseArchitectureTheme {
        OtpInputViewType2(onOtpComplete = {}, onDoneClick = {}, onCurrentOtpLength = {})
    }
}