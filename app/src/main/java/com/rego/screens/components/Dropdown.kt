package com.rego.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.R
import com.rego.ui.theme.Color1A1A1A_60

@Composable
fun DropdownField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDropdownExpand: () -> Unit,
    expanded: Boolean,
    options: List<String>,
    leadingIcon: Int? = null,
    placeholder: String = "",
    onDismissRequest: () -> Unit,
    labelComposable: (@Composable () -> Unit)? = null
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = label,
                    tint = Color1A1A1A_60(),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            if (labelComposable != null) {
                labelComposable()
            } else {
                Text(
                    text = label,
                    color = Color1A1A1A_60(),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onDropdownExpand() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    color = if (value.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Dropdown arrow",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp)
                )
            }
            DropdownMenu(
                modifier = Modifier.background(Color.White),
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = option,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}