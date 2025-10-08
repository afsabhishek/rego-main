package com.rego.screens.raiserequest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.screens.components.RegoButton
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme

@Composable
fun RequestSubmittedScreen(
    onOkayClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick),
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Request Submitted",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Thank you for your request. Kindly await approval or disapproval from REGO CR",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color1A1A1A_60(),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        // Okay Button
        RegoButton(
            onClick = onOkayClick,
            text = "Okay"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RequestSubmittedScreenPreview() {
    NativeAndroidBaseArchitectureTheme {
        RequestSubmittedScreen()
    }
}