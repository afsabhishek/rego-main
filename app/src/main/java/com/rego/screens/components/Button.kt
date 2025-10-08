package com.rego.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.NativeAndroidBaseArchitectureTheme

@Composable
fun RegoButton(onClick: () -> Unit, text: String, enabled: Boolean = true, height: Dp = 44.dp) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color00954D,
            disabledContainerColor = Color00954D.copy(alpha = 0.6f)
        ),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun RegoButtonPreview() {
    NativeAndroidBaseArchitectureTheme {
        RegoButton(onClick = { /*TODO*/ }, text = "Button Text", enabled = false)
    }
}