package com.rego.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.ui.theme.Color1A1A1A
import com.rego.ui.theme.Color1A1A1A_40
import com.rego.ui.theme.Color1A1A1A_60

@Composable
fun TransparentInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: Int? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    labelComposable: (@Composable () -> Unit)? = null
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = label,
                    tint = Color1A1A1A_60(),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            if (labelComposable != null) {
                labelComposable()
            } else {
                Text(
                    text = label,
                    color = Color1A1A1A_60(),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        BasicTextField(
            value = value,
            modifier = Modifier.height(40.dp),
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 14.sp, color = Color1A1A1A),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color1A1A1A_40(),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}