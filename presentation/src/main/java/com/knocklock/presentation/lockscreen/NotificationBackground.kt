package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

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
    updateNotificationClickableFlag: (String, Boolean) -> Unit = { _, _ -> },
    updateNotificationExpandableFlag: (String, RemovedType) -> Unit = { _, _ -> },
    onRemoveNotification: (RemovedGroupNotification) -> Unit = {},
    onNotificationClicked: (PendingIntent) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    val animateOffsetY = remember { Animatable(0f) }

    Box {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(notificationHeight)
                .graphicsLayer {
                    var offsetY = 0f
                    var currentOffset = if (threshold < offset()) threshold / offset() else 1f
                    updateNotificationClickableFlag(item.group.key, (item.notifications.size >= 2 && offset() < threshold))
                    if (currentOffset == 1f) {
                        coroutineScope.launch {
                            animateOffsetY.animateTo(0f, tween()) {
                                offsetY = value
                            }
                        }
                    }

                    translationY = offsetY
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
                    var offsetY = 0f
                    var currentOffset = if (threshold < offset()) threshold / offset() else 1f
                    println("${notification.appTitle} ${notification.title} currentOffset : $currentOffset")
                    updateNotificationClickableFlag(item.group.key, (item.notifications.size >= 2 && offset() < threshold))
                    if (currentOffset == 1f) {
                        coroutineScope.launch {
                            animateOffsetY.animateTo(0f, tween()) {
                                offsetY = value
                            }
                        }
                    }
                    translationY = offsetY
                    translationX = offsetX
                    alpha = currentOffset
                    scaleX = currentOffset
                    scaleY = currentOffset
                }
                .clickable(
                    enabled = clickable,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    updateNotificationExpandableFlag(item.group.key, type)
                }
                .animateItemPlacement(),
            onRemoveNotification = onRemoveNotification,
            notification = notification,
            clickableState = clickable,
            expandableState = expandable,
            groupNotification = item.notifications.toImmutableList(),
            onNotificationClicked = onNotificationClicked,
            updateSwipeOffset = {
                offsetX = it
            },
            type = RemovedType.Old,
        )

//        if (clickableState) {
//            Canvas(
//                modifier = Modifier.fillMaxWidth().height(notificationHeight)
//                    .graphicsLayer {
//                        translationY = offsetY + (notificationHeight.toPx() / 4)
//                        translationX = offsetX
//                        alpha = currentOffset
//                        scaleX = currentOffset / 4 * 3
//                        scaleY = currentOffset / 4 * 3
//                    }.animateItemPlacement(),
//            ) {
//                drawRoundRect(
//                    color = animateColor,
//                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
//                )
//            }
//        }
    }
}
