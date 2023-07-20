package com.knocklock.presentation.lockscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.knocklock.domain.model.TimeFormat
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType.Old
import com.knocklock.presentation.lockscreen.model.RemovedType.Recent
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.rememberSwipeableState
import com.knocklock.presentation.lockscreen.util.swipeable
import com.knocklock.presentation.widget.ClockWidget
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
    recentNotificationList: ImmutableList<GroupWithNotification>,
    recentNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    oldNotificationUiState: NotificationUiState,
    oldNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    userSwipe: () -> Unit,
    onNotificationRemove: (RemovedGroupNotification) -> Unit,
    onNotificationClick: (String) -> Unit,
    updateOldNotificationExpandableFlag: (String) -> Unit,
    updateRecentNotificationExpandableFlag: (String) -> Unit,
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
) {
    LockScreen(
        modifier = modifier,
        recentNotificationList = recentNotificationList,
        recentNotificationUiFlagState = recentNotificationUiFlagState,
        oldNotificationUiState = oldNotificationUiState,
        oldNotificationUiFlagState = oldNotificationUiFlagState,
        userSwipe = userSwipe,
        onNotificationRemove = onNotificationRemove,
        onNotificationClick = onNotificationClick,
        updateNotificationExpandableFlag = updateOldNotificationExpandableFlag,
        updateNewNotificationExpandableFlag = updateRecentNotificationExpandableFlag,
        updateNotificationClickableFlag = updateNotificationClickableFlag,
        timeFormat = timeFormat,
    )
}

@Composable
fun LockScreen(
    recentNotificationList: ImmutableList<GroupWithNotification>,
    recentNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    oldNotificationUiState: NotificationUiState,
    oldNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    userSwipe: () -> Unit,
    onNotificationRemove: (RemovedGroupNotification) -> Unit,
    onNotificationClick: (String) -> Unit,
    updateNotificationExpandableFlag: (String) -> Unit,
    updateNewNotificationExpandableFlag: (String) -> Unit,
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (oldNotificationUiState) {
                is NotificationUiState.Success -> {
                    LockScreenNotificationListColumn(
                        recentNotificationList = recentNotificationList,
                        recentNotificationUiFlagState = recentNotificationUiFlagState,
                        oldGroupNotificationList = oldNotificationUiState.groupWithNotification.toImmutableList(),
                        oldNotificationUiFlagState = oldNotificationUiFlagState,
                        onNotificationRemove = onNotificationRemove,
                        onNotificationClick = onNotificationClick,
                        onOldNotificationExpandableFlagUpdate = updateNotificationExpandableFlag,
                        onRecentNotificationExpandableFlagUpdate = updateNewNotificationExpandableFlag,
                        onNotificationClickableFlagUpdate = updateNotificationClickableFlag,
                        notificationHeight = 60.dp,
                        timeFormat = timeFormat,
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

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val movingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = height.value,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
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
    recentNotificationList: ImmutableList<GroupWithNotification>,
    recentNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    oldGroupNotificationList: ImmutableList<GroupWithNotification>,
    oldNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    onOldNotificationExpandableFlagUpdate: (String) -> Unit,
    onRecentNotificationExpandableFlagUpdate: (String) -> Unit,
    onNotificationRemove: (RemovedGroupNotification) -> Unit,
    notificationHeight: Dp,
    onNotificationClickableFlagUpdate: (String, Boolean) -> Unit,
    onNotificationClick: (String) -> Unit,
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
) {
    val lockNotiModifier = modifier
        .clip(RoundedCornerShape(10.dp))
    val scrollState = rememberLazyListState()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeight = with(density) {
        configuration.screenHeightDp.dp.roundToPx()
    }
    val threshold = screenHeight.times(0.7f)

    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 500.dp, start = 10.dp, end = 10.dp, top = 100.dp),
    ) {
        item {
            ClockWidget(
                timeFormat = timeFormat,
            )
        }
        if (recentNotificationList.isNotEmpty()) {
            item {
                Text(text = " 최근 알림 입니다 ")
            }
        }
        recentNotificationList.forEach { item ->

            item {
                if (recentNotificationUiFlagState.containsKey(item.group.key) && recentNotificationUiFlagState[item.group.key]!!.expandable) {
                    LockScreenGroupInfo(
                        groupTitle = item.notifications[0].appTitle,
                        notifications = item.notifications.toImmutableList(),
                        onRemoveNotification = onNotificationRemove,
                        removeType = Recent,
                        onDisableExpand = {
                            with(item.group.key) {
                                if (recentNotificationUiFlagState.containsKey(this)) {
                                    onRecentNotificationExpandableFlagUpdate(this)
                                }
                            }
                        },
                    )
                }
            }

            item(key = item.notifications[0].groupKey + item.notifications[0].postedTime) {
                Notification(
                    modifier = lockNotiModifier,
                    notificationHeight = notificationHeight,
                    clickable = recentNotificationUiFlagState[item.group.key]?.clickable ?: false,
                    offset = {
                        scrollState.layoutInfo.visibleItemsInfo.firstOrNull {
                            it.key == item.notifications[0].groupKey + item.notifications[0].postedTime
                        }?.offset ?: Integer.MAX_VALUE
                    },
                    threshold = threshold,
                    item = item,
                    onNotificationClickableFlagUpdate = onNotificationClickableFlagUpdate,
                    type = Recent,
                    expandable = recentNotificationUiFlagState[item.group.key]?.expandable ?: false,
                    onNotificationExpandableFlagUpdate = { key, type ->
                        when (type) {
                            Recent -> {
                                if (recentNotificationUiFlagState.containsKey(key)) {
                                    onRecentNotificationExpandableFlagUpdate(key)
                                }
                            }
                            Old -> {
                                if (oldNotificationUiFlagState.containsKey(key)) {
                                    onOldNotificationExpandableFlagUpdate(key)
                                }
                            }
                        }
                    },
                    onNotificationRemove = onNotificationRemove,
                    onNotificationClick = onNotificationClick,
                    notification = item.notifications[0],

                )
            }
            if (recentNotificationUiFlagState.containsKey(item.group.key) && recentNotificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.groupKey + notification.postedTime }) { notification ->
                    Notification(
                        notificationHeight = notificationHeight,
                        offset = { scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + notification.postedTime }?.offset ?: Integer.MAX_VALUE },
                        threshold = threshold,
                        item = item,
                        modifier = lockNotiModifier,
                        type = Recent,
                        expandable = recentNotificationUiFlagState[item.group.key]?.expandable ?: false,
                        onNotificationExpandableFlagUpdate = { key, type ->
                            when (type) {
                                Recent -> {
                                    if (recentNotificationUiFlagState.containsKey(key)) {
                                        onRecentNotificationExpandableFlagUpdate(key)
                                    }
                                }
                                Old -> {
                                    if (oldNotificationUiFlagState.containsKey(key)) {
                                        onOldNotificationExpandableFlagUpdate(key)
                                    }
                                }
                            }
                        },
                        onNotificationRemove = onNotificationRemove,
                        onNotificationClick = onNotificationClick,
                        clickable = false,
                        notification = notification,
                    )
                }
            }
        }

        if (oldGroupNotificationList.isNotEmpty()) {
            item {
                Text(text = " 오래된 알림 입니다 ")
            }
        }

        oldGroupNotificationList.forEach { item ->

            item {
                if (oldNotificationUiFlagState.containsKey(item.group.key) && oldNotificationUiFlagState[item.group.key]!!.expandable) {
                    LockScreenGroupInfo(
                        groupTitle = item.notifications[0].appTitle,
                        notifications = item.notifications.toImmutableList(),
                        onRemoveNotification = onNotificationRemove,
                        removeType = Old,
                        onDisableExpand = {
                            with(item.group.key) {
                                if (oldNotificationUiFlagState.containsKey(this)) {
                                    onOldNotificationExpandableFlagUpdate(this)
                                }
                            }
                        },
                    )
                }
            }

            item(key = item.notifications[0].groupKey + item.notifications[0].postedTime) {
                Notification(
                    notificationHeight = notificationHeight,
                    clickable = oldNotificationUiFlagState[item.group.key]?.clickable ?: false,
                    offset = { scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + item.notifications[0].postedTime }?.offset ?: Integer.MAX_VALUE },
                    threshold = threshold,
                    item = item,
                    onNotificationClickableFlagUpdate = onNotificationClickableFlagUpdate,
                    modifier = lockNotiModifier,
                    type = Old,
                    expandable = oldNotificationUiFlagState[item.group.key]?.expandable ?: false,
                    onNotificationExpandableFlagUpdate = { key, type ->
                        when (type) {
                            Recent -> {
                                if (recentNotificationUiFlagState.containsKey(key)) {
                                    onRecentNotificationExpandableFlagUpdate(key)
                                }
                            }
                            Old -> {
                                if (oldNotificationUiFlagState.containsKey(key)) {
                                    onOldNotificationExpandableFlagUpdate(key)
                                }
                            }
                        }
                    },
                    onNotificationRemove = onNotificationRemove,
                    onNotificationClick = onNotificationClick,
                    notification = item.notifications[0],
                )
            }
            if (oldNotificationUiFlagState.containsKey(item.group.key) && oldNotificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.groupKey + notification.postedTime }) { notification ->

                    Notification(
                        notificationHeight = notificationHeight,
                        offset = { scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + notification.postedTime }?.offset ?: Integer.MAX_VALUE },
                        threshold = threshold,
                        item = item,
                        modifier = lockNotiModifier,
                        type = Old,
                        expandable = oldNotificationUiFlagState[item.group.key]?.expandable ?: false,
                        onNotificationExpandableFlagUpdate = { key, type ->
                            when (type) {
                                Recent -> {
                                    if (recentNotificationUiFlagState.containsKey(key)) {
                                        onRecentNotificationExpandableFlagUpdate(key)
                                    }
                                }
                                Old -> {
                                    if (oldNotificationUiFlagState.containsKey(key)) {
                                        onOldNotificationExpandableFlagUpdate(key)
                                    }
                                }
                            }
                        },
                        onNotificationRemove = onNotificationRemove,
                        onNotificationClick = onNotificationClick,
                        clickable = false,
                        notification = notification,
                    )
                }
            }
        }
    }
}
