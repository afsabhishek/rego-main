package com.rego.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rego.ui.theme.Color1A1A1A_60
import com.rego.ui.theme.Color1A1A1A_90
import com.rego.ui.theme.fontMediumPoppins

@Composable
fun LabelValuePair(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = fontMediumPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_60())
            )
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = fontMediumPoppins().copy(fontSize = 12.sp, color = Color1A1A1A_90())
                )
                if (trailing != null) {
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    trailing()
                }
            }
        }
    }
}
