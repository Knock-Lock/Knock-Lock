package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType
import com.knocklock.presentation.lockscreen.util.DismissValue
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.SwipeToDismiss
import com.knocklock.presentation.lockscreen.util.rememberDismissState
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:06 PM
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissLockNotiItem(
    onNotificationRemove: (RemovedGroupNotification) -> Unit,
    notification: Notification,
    clickableState: Boolean,
    expandableState: Boolean,
    type: RemovedType,
    modifier: Modifier = Modifier,
    groupNotification: ImmutableList<Notification>? = null,
) {
    val updateGroupNotification by rememberUpdatedState(newValue = groupNotification)
    val updateNotification by rememberUpdatedState(newValue = notification)
    val updateExpandableState by rememberUpdatedState(newValue = expandableState)
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
        if (updateNotification.isClearable) {
            when (dismissValue) {
                DismissValue.Default -> {
                    false
                }
                DismissValue.DismissedToStart -> {
                    false
                }
                DismissValue.DismissedToEnd -> {
                    coroutineScope.launch {
                        delay(120)
                        onNotificationRemove(
                            RemovedGroupNotification(
                                key = updateNotification.groupKey,
                                type = type,
                                removedNotifications = if (updateExpandableState) {
                                    listOf(
                                        updateNotification,
                                    )
                                } else {
                                    updateGroupNotification?.toList() ?: emptyList()
                                },
                            ),
                        )
                    }
                    true
                }
            }
        } else {
            false
        }
    })

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.25f) },
        dismissContent = {
            LockNotiItem(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White.copy(alpha = 0.8f),
                        RoundedCornerShape(16.dp),
                    ),
                notification = updateNotification,
                clickableState = clickableState,
                expandableState = expandableState,
            )
        },
        background = {},
    )
}

@Composable
fun LockNotiItem(
    notification: Notification,
    clickableState: Boolean,
    expandableState: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(start = 9.dp, top = 9.dp, bottom = 9.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (notification.packageName != null) {
            Image(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)),
                painter = rememberDrawablePainter(
                    drawable = LocalContext.current.packageManager.getApplicationIcon(notification.packageName),
                ),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 2.5.dp, bottom = 2.5.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            LockNotiTop(
                notificationTitle = notification.title,
                time = notification.notiTime,
            )
            Spacer(modifier = Modifier.height(1.dp))
            LockNotiContent(
                modifier = Modifier.wrapContentHeight(),
                content = notification.content,
            )
        }
    }
}

@Composable
fun LockNotiTop(
    notificationTitle: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.7f),
                text = notificationTitle,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.W700,
            )
        }
        Text(
            text = time,
            fontSize = 10.sp,
        )
    }
}

@Composable
fun LockNotiContent(
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = content,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            fontSize = 13.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLockNotiItem() {
    KnockLockTheme {
        LockNotiItem(
            notification = Notification.default,
            clickableState = false,
            expandableState = false,
        )
    }
}
