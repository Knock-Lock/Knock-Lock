package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType
import kotlinx.collections.immutable.toImmutableList

/**
 * @Created by 김현국 2023/07/06
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.Notification(
    notificationHeight: Dp,
    offset: () -> Int,
    threshold: Float,
    item: GroupWithNotification,
    notification: Notification,
    type: RemovedType,
    expandable: Boolean,
    clickable: Boolean,
    modifier: Modifier = Modifier,
    onNotificationClickableFlagUpdate: (String, Boolean) -> Unit = { _, _ -> },
    onNotificationExpandableFlagUpdate: (String, RemovedType) -> Unit = { _, _ -> },
    onNotificationRemove: (RemovedGroupNotification) -> Unit = {},
    onNotificationClick: (String) -> Unit = {},
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier.padding(bottom = if (item.notifications.size >= 2)4.dp else 0.dp).animateItemPlacement(),
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(notificationHeight)
                .graphicsLayer {
                    val currentOffset = if (threshold < offset()) threshold / offset() else 1f
                    onNotificationClickableFlagUpdate(item.group.key, (item.notifications.size >= 2 && offset() < threshold))
                    translationX = offsetX
                    alpha = currentOffset
                    scaleX = currentOffset
                    scaleY = currentOffset
                }
                .animateItemPlacement(),
        ) {
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
            )
        }

        SwipeToDismissLockNotiItem(
            modifier = modifier
                .graphicsLayer {
                    val currentOffset = if (threshold < offset()) threshold / offset() else 1f
                    onNotificationClickableFlagUpdate(item.group.key, (item.notifications.size >= 2 && offset() < threshold))
                    alpha = currentOffset
                    scaleX = currentOffset
                    scaleY = currentOffset
                }
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    if (clickable && !expandable) {
                        onNotificationExpandableFlagUpdate(item.group.key, type)
                    } else {
                        notification.packageName?.let { onNotificationClick(it) }
                    }
                }
                .animateItemPlacement(),
            onNotificationRemove = onNotificationRemove,
            notification = notification,
            clickableState = clickable,
            expandableState = expandable,
            groupNotification = item.notifications.toImmutableList(),
            updateSwipeOffset = {
                offsetX = it
            },
            type = type,
        )

        if (item.notifications.size >= 2 && clickable && !expandable) {
            Canvas(
                modifier = Modifier.fillMaxWidth().height(notificationHeight)
                    .graphicsLayer {
                        val currentOffset = if (threshold < offset()) threshold / offset() else 1f
                        translationY = (notificationHeight.toPx() / 4)
                        translationX = offsetX
                        alpha = currentOffset
                        scaleX = currentOffset / 4 * 3
                        scaleY = currentOffset / 4 * 3
                    }.zIndex(-1f).animateItemPlacement(),
            ) {
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            }
        }
    }
}
