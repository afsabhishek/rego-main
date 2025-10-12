package com.rego.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.ColorE7503D
import com.rego.ui.theme.fontSemiBoldPoppins

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmButtonText: String = "OK"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.error),
                contentDescription = "Error",
                tint = ColorE7503D,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = fontSemiBoldPoppins().copy(fontSize = 18.sp),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                style = fontSemiBoldPoppins().copy(
                    fontSize = 14.sp,
                    color = Color1A1A1A_60()
                ),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(confirmButtonText)
            }
        }
    )
}

@Composable
fun ErrorScreen(
    title: String,
    message: String,
    buttonText: String = "Retry",
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.error),
                contentDescription = "Error",
                tint = ColorE7503D,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                style = fontSemiBoldPoppins().copy(fontSize = 18.sp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = message,
                style = fontSemiBoldPoppins().copy(
                    fontSize = 14.sp,
                    color = Color1A1A1A_60()
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            RegoButton(
                onClick = onRetry,
                text = buttonText
            )
        }
    }
}

@Composable
fun CustomSnackbar(
    message: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Snackbar(
        action = {
            if (actionLabel != null) {
                TextButton(onClick = {
                    onActionClick()
                    onDismiss()
                }) {
                    Text(actionLabel)
                }
            }
        },
        dismissAction = {
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Dismiss"
                )
            }
        }
    ) {
        Text(message)
    }
}