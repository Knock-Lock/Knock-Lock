package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    offset: () -> Int,
    threshold: Float,
    item: GroupWithNotification,
    notification: Notification,
    type: RemovedType,
    expandable: Boolean,
    clickable: Boolean,
    onNotificationClickableFlagUpdate: (String, Boolean) -> Unit = { _, _ -> },
    onNotificationExpandableFlagUpdate: (String, RemovedType) -> Unit = { _, _ -> },
    onNotificationRemove: (RemovedGroupNotification) -> Unit = {},
    onNotificationClick: (String) -> Unit = {},
) {
    SwipeToDismissLockNotiItem(
        modifier = Modifier.fillMaxHeight()
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
        type = type,
    )
}
