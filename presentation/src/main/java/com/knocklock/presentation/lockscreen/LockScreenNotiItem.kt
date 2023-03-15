package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.util.DismissValue
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.SwipeToDismiss
import com.knocklock.presentation.lockscreen.util.rememberDismissState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:06 PM
 */

@Composable
fun GroupLockNotiItem(
    modifier: Modifier = Modifier,
    notificationList: ImmutableList<Notification>,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit
) {
    if (notificationList.isEmpty()) {
        return
    }
    val notification by rememberUpdatedState(notificationList[0])
    var clickableState by remember { mutableStateOf(false) }
    var expandableState by remember { mutableStateOf(false) }
    LaunchedEffect(notificationList.size) {
        clickableState = notificationList.size >= 2
        if (notificationList.isEmpty()) {
            expandableState = false
        }
    }
    Column(
        modifier = modifier.clickable(
            enabled = clickableState,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            expandableState = !expandableState
        }
    ) {
        val lockNotiModifier = modifier
            .background(
                color = Color(0xFFFAFAFA).copy(alpha = 0.95f),
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
        key(notification.postedTime) {
            SwipeToDismissLockNotiItem(
                modifier = lockNotiModifier,
                onRemoveNotification = onRemoveNotification,
                notification = notification,
                notificationSize = notificationList.size,
                clickableState = clickableState,
                expandableState = expandableState,
                groupNotification = notificationList.toImmutableList(),
                onNotificationClicked = onNotificationClicked
            )
        }

        AnimatedVisibility(visible = expandableState && notificationList.size != 1) {
            Column(
                modifier = modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (index in 1 until notificationList.size) {
                    key(notificationList[index].postedTime) {
                        SwipeToDismissLockNotiItem(
                            modifier = lockNotiModifier,
                            onRemoveNotification = { list ->
                                onRemoveNotification(list)
                            },
                            notification = notificationList[index],
                            notificationSize = notificationList.size,
                            clickableState = false,
                            expandableState = expandableState,
                            onNotificationClicked = onNotificationClicked
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissLockNotiItem(
    modifier: Modifier = Modifier,
    onNotificationClicked: (PendingIntent) -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    notification: Notification,
    notificationSize: Int,
    clickableState: Boolean,
    expandableState: Boolean,
    groupNotification: ImmutableList<Notification>? = null
) {
    val updateGroupNotification by rememberUpdatedState(newValue = groupNotification)
    val updateNotification by rememberUpdatedState(newValue = notification)
    val updateExpandableState by rememberUpdatedState(newValue = expandableState)
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
                    if (updateExpandableState) {
                        onRemoveNotification(listOf(updateNotification.id))
                    } else {
                        updateGroupNotification?.let { childNotificationList ->
                            onRemoveNotification(
                                childNotificationList.map { notification ->
                                    notification.id
                                }
                            )
                        }
                    }
                    true
                }
            }
        } else {
            false
        }
    })
    SwipeToDismiss(
        modifier = Modifier,
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.25f) },
        dismissContent = {
            Column {
                Box {
                    LockNotiItem(
                        modifier = modifier,
                        notification = updateNotification
                    )
                    if (clickableState) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 5.dp),
                            imageVector = if (expandableState) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
                AnimatedVisibility(visible = !expandableState) {
                    Column {
                        if (notificationSize == 2) {
                            MoreNotification(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .fillMaxWidth()
                                    .height(7.dp)
                            )
                        } else if (notificationSize >= 3) {
                            MoreNotification(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .fillMaxWidth()
                                    .height(7.dp)
                            )
                            MoreNotification(
                                modifier = Modifier
                                    .padding(horizontal = 35.dp)
                                    .fillMaxWidth()
                                    .height(5.dp)
                            )
                        }
                    }
                }
            }
        },
        background = {}
    )
}

@Composable
fun MoreNotification(
    modifier: Modifier = Modifier
) {
    val moreNotificationShape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
    Row(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFAFAFA).copy(alpha = 0.9f),
                        Color.LightGray
                    )
                ),
                shape = moreNotificationShape
            )
            .clip(shape = moreNotificationShape)
    ) {}
}

@Composable
fun LockNotiItem(
    modifier: Modifier = Modifier,
    notification: Notification
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LockNotiTop(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(top = 4.dp),
            packageName = notification.packageName,
            appTitle = notification.appTitle,
            time = notification.notiTime
        )
        LockNotiContent(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 4.dp)
                .wrapContentHeight(),
            title = notification.title,
            content = notification.content
        )
    }
}

@Composable
fun LockNotiTop(
    modifier: Modifier = Modifier,
    packageName: String?,
    appTitle: String,
    time: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (packageName != null) {
                Image(
                    modifier = Modifier.size(10.dp),
                    painter = rememberDrawablePainter(
                        drawable = LocalContext.current.packageManager.getApplicationIcon(packageName)
                    ),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = appTitle,
                fontSize = 10.sp
            )
        }
        Text(
            text = time,
            fontSize = 10.sp
        )
    }
}

@Composable
fun LockNotiContent(
    modifier: Modifier = Modifier,
    title: String,
    content: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.W700
        )
        Text(
            content,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
