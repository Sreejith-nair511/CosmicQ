package com.example.cosmoq1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.data.CardCategory
import com.example.cosmoq1.ui.theme.*

@Composable
fun CategoryBadge(category: CardCategory) {
    val (label, color) = when (category) {
        CardCategory.NEWS      -> "News"      to BadgeNews
        CardCategory.ROCKET    -> "Rocket"    to BadgeRocket
        CardCategory.DISCOVERY -> "Discovery" to BadgeDiscovery
        CardCategory.MISSION   -> "Mission"   to BadgeMission
        CardCategory.NASA      -> "NASA"      to BadgeNasa
    }
    Text(
        text = label,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier
            .background(color.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
            .border(1.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
