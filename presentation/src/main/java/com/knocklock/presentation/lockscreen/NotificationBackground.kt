package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * @Created by 김현국 2023/07/06
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.NotificationBackground(
    offsetX: Float,
    offsetY: Float,
    currentOffset: Float,
    notificationHeight: Dp,
    animateColor: Color,
) {
    Canvas(
        modifier = Modifier.fillMaxWidth().height(notificationHeight)
            .graphicsLayer {
                translationY = offsetY
                translationX = offsetX
                alpha = currentOffset
                scaleX = currentOffset
                scaleY = currentOffset
            }
            .zIndex(offsetY).animateItemPlacement(),
    ) {
        drawRoundRect(
            color = animateColor,
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
        )
    }
}
