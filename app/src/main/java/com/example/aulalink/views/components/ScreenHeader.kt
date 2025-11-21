package com.example.aulalink.views.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulalink.ui.theme.AccentRed
import com.example.aulalink.ui.theme.TextPrimary
import com.example.aulalink.ui.theme.TextSecondary

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "AulaLink",
            color = AccentRed,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
        subtitle?.let {
            Text(
                text = it,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )
        }
    }
}
