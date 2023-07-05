package com.knocklock.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetHeaderBar(
    modifier: Modifier = Modifier,
    barColor: Color = Color.LightGray,
) {
    Spacer(
        modifier = modifier
            .width(40.dp)
            .height(6.dp)
            .background(color = barColor, shape = RoundedCornerShape(3.dp))
    )
}