package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onNotificationClicked: (PendingIntent) -> Unit,
    onRemoveNotification: (RemovedGroupNotification) -> Unit,
    notification: Notification,
    clickableState: Boolean,
    expandableState: Boolean,
    type: RemovedType,
    updateSwipeOffset: (Float) -> Unit,
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

                        onRemoveNotification(
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

    LaunchedEffect(dismissState) {
        snapshotFlow { dismissState.offset.value }.collect {
            updateSwipeOffset(it)
        }
    }
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.25f) },
        dismissContent = {
            LockNotiItem(
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
    modifier: Modifier = Modifier,
    notification: Notification,
    clickableState: Boolean,
    expandableState: Boolean,
) {
    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            LockNotiTop(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 4.dp),
                packageName = notification.packageName,
                appTitle = notification.appTitle,
                time = notification.notiTime,
            )
            LockNotiContent(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 4.dp)
                    .wrapContentHeight(),
                title = notification.title,
                content = notification.content,
            )
        }
        if (clickableState && !expandableState) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 5.dp),
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun LockNotiTop(
    modifier: Modifier = Modifier,
    packageName: String?,
    appTitle: String,
    time: String,
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
            if (packageName != null) {
                Image(
                    modifier = Modifier.size(10.dp),
                    painter = rememberDrawablePainter(
                        drawable = LocalContext.current.packageManager.getApplicationIcon(packageName),
                    ),
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = appTitle,
                fontSize = 10.sp,
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
    modifier: Modifier = Modifier,
    title: String,
    content: String,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.W700,
        )
        Text(
            content,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}
