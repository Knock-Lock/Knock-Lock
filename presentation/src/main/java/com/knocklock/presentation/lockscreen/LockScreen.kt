package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import android.widget.TextClock
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.rememberSwipeableState
import com.knocklock.presentation.lockscreen.util.swipeable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:01 PM
 */

@Composable
fun LockScreenRoute(
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState,
    notificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    userSwipe: () -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
    updateNotificationExpandableFlag: (String) -> Unit,
) {
    LockScreen(
        modifier = modifier,
        notificationUiState = notificationUiState,
        notificationUiFlagState = notificationUiFlagState,
        userSwipe = userSwipe,
        onRemoveNotification = onRemoveNotification,
        onNotificationClicked = onNotificationClicked,
        updateNotificationExpandableFlag = updateNotificationExpandableFlag,
    )
}

@Composable
fun LockScreen(
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState,
    notificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    userSwipe: () -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
    updateNotificationExpandableFlag: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)) // Image로 추후 변경
        Spacer(modifier = Modifier.height(50.dp))
        TextClockComposable(modifier = Modifier.align(Alignment.CenterHorizontally))
        Box(modifier = Modifier.fillMaxSize()) {
            when (notificationUiState) {
                is NotificationUiState.Success -> {
                    LockScreenNotificationListColumn(
                        groupNotificationList = notificationUiState.groupWithNotification.toImmutableList(),
                        notificationUiFlagState = notificationUiFlagState,
                        onRemoveNotification = onRemoveNotification,
                        onNotificationClicked = onNotificationClicked,
                        updateNotificationExpandableFlag = updateNotificationExpandableFlag,
                    )
                }
                is NotificationUiState.Empty -> {
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter),
            ) {
                UnLockSwipeBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .zIndex(2f),
                    width = 200.dp,
                    height = 100.dp,
                    userSwipe = userSwipe,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnLockSwipeBar(
    modifier: Modifier = Modifier,
    width: Dp,
    height: Dp,
    userSwipe: () -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)

    val sizePx = with(LocalDensity.current) { height.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)

    val infiniteTransition = rememberInfiniteTransition()
    val movingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = height.value,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    val targetValue by remember {
        derivedStateOf {
            swipeableState.targetValue
        }
    }


    LaunchedEffect(targetValue) {
        snapshotFlow { targetValue }
            .collect {
                if (targetValue == 1) {
                    userSwipe()
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth() // touch 영역
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical,
            ),
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Box(
                Modifier
                    .offset { IntOffset(0, -swipeableState.offset.value.roundToInt()) }
                    .offset { IntOffset(0, movingAnimation.toInt()) }
                    .width(width)
                    .height(5.dp)
                    .background(Color.White),
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
        }
    }
}

@Composable
fun LockScreenNotificationListColumn(
    modifier: Modifier = Modifier,
    groupNotificationList: ImmutableList<GroupWithNotification>,
    notificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    updateNotificationExpandableFlag: (String) -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
) {
    val lockNotiModifier = modifier
        .background(
            color = Color(0xFFFAFAFA).copy(alpha = 0.95f),
            shape = RoundedCornerShape(10.dp),
        )
        .clip(RoundedCornerShape(10.dp))

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(10.dp),
    ) {
        groupNotificationList.forEach { item ->
            item(key = item.notifications[0].postedTime) {
                SwipeToDismissLockNotiItem(
                    modifier = lockNotiModifier.clickable(
                        enabled = notificationUiFlagState[item.group.key]?.clickable ?: false,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        if (notificationUiFlagState.containsKey(item.group.key)) {
                            notificationUiFlagState[item.group.key]?.let { state ->
                                updateNotificationExpandableFlag(
                                    item.group.key,
                                )
                            }
                        }
                    },
                    onRemoveNotification = onRemoveNotification,
                    notification = item.notifications[0],
                    notificationSize = item.notifications.size,
                    clickableState = notificationUiFlagState[item.group.key]?.clickable ?: false,
                    expandableState = notificationUiFlagState[item.group.key]?.expandable ?: false,
                    groupNotification = item.notifications.toImmutableList(),
                    onNotificationClicked = onNotificationClicked,
                )
            }
            if (notificationUiFlagState.containsKey(item.group.key) && notificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.postedTime }) { notification ->
                    SwipeToDismissLockNotiItem(
                        modifier = lockNotiModifier,
                        onNotificationClicked = onNotificationClicked,
                        onRemoveNotification = {
                            onRemoveNotification(it)
                        },
                        notification = notification,
                        notificationSize = item.notifications.size,
                        clickableState = false,
                        expandableState = notificationUiFlagState[item.group.key]?.expandable ?: false,
                    )
                }
            }
        }
    }
}

@Composable
fun TextClockComposable(
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour?.let { this.format12Hour = "hh:mm:ss a" }
                timeZone?.let { this.timeZone = it }
                textSize.let { this.textSize = 30f }
            }
        },
        // on below line we are adding padding.
        modifier = modifier,
    )
}

fun updateExpandable(expandableState: SnapshotStateMap<String, Boolean>, key: String) {
    if (!expandableState.containsKey(key)) {
        expandableState[key] = false
    }
}
fun updateClickable(clickableState: SnapshotStateMap<String, Boolean>, key: String, flag: Boolean) {
    clickableState[key] = flag
}
