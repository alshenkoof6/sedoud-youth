package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FollowupStatus
import com.example.ui.theme.*

@Composable
fun FollowupStatusBadge(
    status: FollowupStatus,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, icon) = when (status) {
        FollowupStatus.GREEN -> Triple(StatusGreenBg, StatusGreen, Icons.Default.CheckCircle)
        FollowupStatus.YELLOW -> Triple(StatusYellowBg, StatusYellow, Icons.Default.Warning)
        FollowupStatus.ORANGE -> Triple(StatusOrangeBg, StatusOrange, Icons.Default.PriorityHigh)
        FollowupStatus.RED -> Triple(StatusRedBg, StatusRed, Icons.Default.Favorite)
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(10.dp))
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 4.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.titleArabic,
            tint = textColor,
            modifier = Modifier.size(if (compact) 14.dp else 16.dp)
        )
        Text(
            text = status.titleArabic,
            color = textColor,
            fontSize = if (compact) 12.sp else 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
