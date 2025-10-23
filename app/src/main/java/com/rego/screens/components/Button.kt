package com.rego.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.ui.theme.Color00954D
import com.rego.ui.theme.Color4CAF50
import com.rego.ui.theme.Color4CAF50_30
import com.rego.ui.theme.ColorFF534A_30
import com.rego.ui.theme.RegoTheme
import com.rego.ui.theme.fontSemiBoldMontserrat

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
            style = fontSemiBoldMontserrat().copy(color = Color.White, fontSize = 16.sp)
        )
    }
}

@Preview
@Composable
fun RegoButtonPreview() {
    RegoTheme {
        RegoButton(onClick = { /*TODO*/ }, text = "Button Text", enabled = false)
    }
}


@Composable
fun AcceptButtonWithText(
    onClick: @Composable () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .background(
                color = Color4CAF50_30(),
                shape = RoundedCornerShape(30.dp)
            )

            .clickable(
                enabled = enabled,
                onClick = { onClick }
            )
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .width(160.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start

    ) {
        Image(
            painter = painterResource(id = R.drawable.accept_tick),
            contentDescription = "Accept drawable"
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            ) {
            Icon(
                painter = painterResource(id = R.drawable.right_double_arrow),
                contentDescription = "Accept",
                tint = Color4CAF50,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = "Accept",
                textAlign = TextAlign.Right,
                style = fontSemiBoldMontserrat().copy(Color4CAF50, fontSize = 12.sp)
            )
        }
    }
}


@Composable
fun RejectButtonWithText(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .background(
                color = ColorFF534A_30(),
                shape = RoundedCornerShape(30.dp)
            )

            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .width(160.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,

        ) {
            Text(
                text = "Reject",
                textAlign = TextAlign.Right,
                style = fontSemiBoldMontserrat().copy(color = Color.Red, fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Icon(

                painter = painterResource(id = R.drawable.double_arrow),
                contentDescription = "Reject",
                tint = Color(0xFFFF5B5B),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
        }
        Image(
            painter = painterResource(id = R.drawable.reject_cross),
            contentDescription = "Reject drawable"
        )
    }
}

// Preview for the buttons
@Preview(showBackground = true)
@Composable
fun RejectButtonPreview() {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        RejectButtonWithText(onClick = {})
        Spacer(modifier = Modifier.height(16.dp))
        AcceptButtonWithText(onClick = {})
    }
}