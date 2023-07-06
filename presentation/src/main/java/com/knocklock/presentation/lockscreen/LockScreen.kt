package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.flow.collectLatest
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
    onRemoveNotification: (RemovedGroupNotification) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
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
        onRemoveNotification = onRemoveNotification,
        onNotificationClicked = onNotificationClicked,
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
    onRemoveNotification: (RemovedGroupNotification) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
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
                        onRemoveNotification = onRemoveNotification,
                        onNotificationClicked = onNotificationClicked,
                        updateOldNotificationExpandableFlag = updateNotificationExpandableFlag,
                        updateRecentNotificationExpandableFlag = updateNewNotificationExpandableFlag,
                        updateNotificationClickableFlag = updateNotificationClickableFlag,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LockScreenNotificationListColumn(
    recentNotificationList: ImmutableList<GroupWithNotification>,
    recentNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    oldGroupNotificationList: ImmutableList<GroupWithNotification>,
    oldNotificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    updateOldNotificationExpandableFlag: (String) -> Unit,
    updateRecentNotificationExpandableFlag: (String) -> Unit,
    onRemoveNotification: (RemovedGroupNotification) -> Unit,
    notificationHeight: Dp,
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
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
                        onRemoveNotification = onRemoveNotification,
                        removeType = Recent,
                        onDisableExpand = {
                            with(item.group.key) {
                                if (recentNotificationUiFlagState.containsKey(this)) {
                                    updateRecentNotificationExpandableFlag(this)
                                }
                            }
                        },
                    )
                }
            }

            item(key = item.notifications[0].groupKey + item.notifications[0].postedTime) {
                var isNotVisible by rememberSaveable { mutableStateOf(true) }
                var currentOffset by rememberSaveable { mutableStateOf(10000f) }
                val animateColor by animateColorAsState(
                    targetValue =
                    if (currentOffset !in 0.8f..1f) {
                        Color.Transparent
                    } else {
                        Color.White.copy(alpha = currentOffset)
                    },
                    label = "",
                )
                var offsetY by remember { mutableStateOf((-50f)) }
                var offsetX by remember { mutableStateOf(0f) }
                val animateOffsetY = remember { Animatable(0f) }

                LaunchedEffect(
                    currentOffset,
                ) {
                    if (currentOffset == 1f) {
                        animateOffsetY.animateTo(0f, tween()) {
                            offsetY = value
                        }
                    } else {
                        animateOffsetY.animateTo(-(1f- currentOffset) * 100, tween()) {
                            offsetY = value
                        }
                    }
                }
                LaunchedEffect(scrollState) {
                    snapshotFlow {
                        scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + item.notifications[0].postedTime }?.offset ?: Integer.MAX_VALUE
                    }.collectLatest { offset ->
                        currentOffset = if (threshold < offset) threshold / offset else 1f
                        isNotVisible = currentOffset !in 0.8f..1f
                        updateNotificationClickableFlag(item.group.key, (item.notifications.size >= 2 && offset < threshold))
                    }
                }

                Box(
                    modifier = Modifier.animateItemPlacement(),
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
                    if (!isNotVisible) {
                        SwipeToDismissLockNotiItem(
                            modifier = lockNotiModifier
                                .graphicsLayer {
                                    translationY = offsetY
                                    alpha = currentOffset
                                    scaleX = currentOffset
                                    scaleY = currentOffset
                                }.clickable(
                                    enabled = recentNotificationUiFlagState[item.group.key]?.clickable ?: false,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    with(item.group.key) {
                                        if (recentNotificationUiFlagState.containsKey(this)) {
                                            updateRecentNotificationExpandableFlag(this)
                                        }
                                    }
                                },
                            onRemoveNotification = onRemoveNotification,
                            notification = item.notifications[0],
                            clickableState = recentNotificationUiFlagState[item.group.key]?.clickable ?: false,
                            expandableState = recentNotificationUiFlagState[item.group.key]?.expandable ?: false,
                            groupNotification = item.notifications.toImmutableList(),
                            onNotificationClicked = onNotificationClicked,
                            updateSwipeOffset = {
                                offsetX = it
                            },
                            type = Recent,
                        )
                    }
                }
            }
            if (recentNotificationUiFlagState.containsKey(item.group.key) && recentNotificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.groupKey + notification.postedTime }) { notification ->

                    var isNotVisible by rememberSaveable { mutableStateOf(true) }
                    var currentOffset by rememberSaveable { mutableStateOf(10000f) }
                    val animateColor by animateColorAsState(
                        targetValue =
                        if (currentOffset !in 0.8f..1f) {
                            Color.Transparent
                        } else {
                            Color.White.copy(alpha = currentOffset)
                        },
                        label = "",
                    )
                    var offsetY by remember { mutableStateOf((-50f)) }
                    var offsetX by remember { mutableStateOf(0f) }
                    val animateOffsetY = remember { Animatable(0f) }

                    LaunchedEffect(
                        currentOffset,
                    ) {
                        if (currentOffset == 1f) {
                            animateOffsetY.animateTo(0f, tween()) {
                                offsetY = value
                            }
                        } else {
                            animateOffsetY.animateTo(-(1f- currentOffset) * 100, tween()) {
                                offsetY = value
                            }
                        }
                    }
                    LaunchedEffect(scrollState) {
                        snapshotFlow {
                            scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == notification.groupKey + item.notifications[0].postedTime }?.offset ?: Integer.MAX_VALUE
                        }.collectLatest { offset ->
                            currentOffset = if (threshold - offset < 0) threshold / offset else 1f
                            isNotVisible = currentOffset !in 0.8f..1f
                            updateNotificationClickableFlag(item.group.key, (item.notifications.size >= 2 && offset < threshold))
                        }
                    }
                    Box(
                        modifier = Modifier.animateItemPlacement(),
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxWidth().height(notificationHeight) // Notification의 배경을 담당
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
                        if (!isNotVisible) {
                            SwipeToDismissLockNotiItem(
                                modifier = lockNotiModifier
                                    .fillMaxWidth().height(60.dp)
                                    .graphicsLayer {
                                        translationY = offsetY
                                        alpha = currentOffset
                                        scaleX = currentOffset
                                        scaleY = currentOffset
                                    }.animateItemPlacement(),
                                onNotificationClicked = onNotificationClicked,
                                onRemoveNotification = onRemoveNotification,
                                notification = notification,
                                clickableState = false,
                                expandableState = recentNotificationUiFlagState[item.group.key]?.expandable ?: false,
                                updateSwipeOffset = {
                                    offsetX = it
                                },
                                type = Recent,
                            )
                        }
                    }
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
                        onRemoveNotification = onRemoveNotification,
                        removeType = Old,
                        onDisableExpand = {
                            with(item.group.key) {
                                if (oldNotificationUiFlagState.containsKey(this)) {
                                    updateOldNotificationExpandableFlag(this)
                                }
                            }
                        },
                    )
                }
            }

            item(key = item.notifications[0].groupKey + item.notifications[0].postedTime) {
                var isNotVisible by rememberSaveable { mutableStateOf(true) }
                var currentOffset by rememberSaveable { mutableStateOf(10000f) }
                val animateColor by animateColorAsState(
                    targetValue =
                    if (currentOffset !in 0.8f..1f) {
                        Color.Transparent
                    } else {
                        Color.White.copy(alpha = currentOffset)
                    },
                    label = "",
                )
                var offsetY by remember { mutableStateOf((-50f)) }
                var offsetX by remember { mutableStateOf(0f) }
                val animateOffsetY = remember { Animatable(0f) }

                LaunchedEffect(
                    currentOffset,
                ) {
                    if (currentOffset == 1f) {
                        animateOffsetY.animateTo(0f, tween()) {
                            offsetY = value
                        }
                    } else {
                        animateOffsetY.animateTo(-(1f- currentOffset) * 100, tween()) {
                            offsetY = value
                        }
                    }
                }
                LaunchedEffect(scrollState) {
                    snapshotFlow {
                        scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + item.notifications[0].postedTime }?.offset ?: Integer.MAX_VALUE
                    }.collectLatest { offset ->
                        currentOffset = if (threshold - offset < 0) threshold / offset else 1f
                        isNotVisible = currentOffset !in 0.8f..1f
                        updateNotificationClickableFlag(item.group.key, (item.notifications.size >= 2 && offset < threshold))
                    }
                }

                Box(
                    modifier = Modifier.animateItemPlacement(),
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxWidth().height(notificationHeight) // Notification의 배경을 담당
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
                    if (!isNotVisible) {
                        SwipeToDismissLockNotiItem(
                            modifier = lockNotiModifier
                                .graphicsLayer {
                                    translationY = offsetY
                                    alpha = currentOffset
                                    scaleX = currentOffset
                                    scaleY = currentOffset
                                }
                                .clickable(
                                    enabled = oldNotificationUiFlagState[item.group.key]?.clickable ?: false,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    with(item.group.key) {
                                        if (oldNotificationUiFlagState.containsKey(this)) {
                                            updateOldNotificationExpandableFlag(this)
                                        }
                                    }
                                }
                                .animateItemPlacement(),
                            onRemoveNotification = onRemoveNotification,
                            notification = item.notifications[0],
                            clickableState = oldNotificationUiFlagState[item.group.key]?.clickable ?: false,
                            expandableState = oldNotificationUiFlagState[item.group.key]?.expandable ?: false,
                            groupNotification = item.notifications.toImmutableList(),
                            onNotificationClicked = onNotificationClicked,
                            updateSwipeOffset = {
                                offsetX = it
                            },
                            type = Old,
                        )
                    }
                }
            }
            if (oldNotificationUiFlagState.containsKey(item.group.key) && oldNotificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.groupKey + notification.postedTime }) { notification ->

                    var isNotVisible by rememberSaveable { mutableStateOf(true) }
                    var currentOffset by remember { mutableStateOf(10000f) }
                    val animateColor by animateColorAsState(
                        targetValue =
                        if (currentOffset !in 0.8f..1f) {
                            Color.Transparent
                        } else {
                            Color.White.copy(alpha = currentOffset)
                        },
                        label = "",
                    )
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf((-50f)) }
                    val animateOffsetY = remember { Animatable(0f) }

                    LaunchedEffect(
                        currentOffset,
                    ) {
                        if (currentOffset == 1f) {
                            animateOffsetY.animateTo(0f, tween()) {
                                offsetY = value
                            }
                        } else {
                            animateOffsetY.animateTo(-(1f - currentOffset) * 100, tween()) {
                                offsetY = value
                            }
                        }
                    }
                    LaunchedEffect(scrollState) {
                        snapshotFlow {
                            scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].groupKey + notification.postedTime }?.offset
                                ?: Integer.MAX_VALUE
                        }.collectLatest { offset ->
                            currentOffset = if (threshold - offset < 0) threshold / offset else 1f
                            isNotVisible = currentOffset !in 0.8f..1f
                        }
                    }
                    Box {
                        Canvas(
                            modifier = Modifier.fillMaxWidth().height(notificationHeight) // Notification의 배경 크기를 담당을 담당
                                .graphicsLayer {
                                    translationY = offsetY
                                    translationX = offsetX
                                    alpha = currentOffset
                                    scaleX = currentOffset
                                    scaleY = currentOffset
                                }
                                .zIndex(offsetY),
                        ) {
                            drawRoundRect(
                                color = animateColor,
                                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                            )
                        }
                        if (!isNotVisible) {
                            SwipeToDismissLockNotiItem(
                                modifier = lockNotiModifier
                                    .fillMaxWidth().height(60.dp)
                                    .graphicsLayer {
                                        translationY = offsetY
                                        alpha = currentOffset
                                        scaleX = currentOffset
                                        scaleY = currentOffset
                                    }.animateItemPlacement(),
                                onNotificationClicked = onNotificationClicked,
                                onRemoveNotification = onRemoveNotification,
                                notification = notification,
                                clickableState = false,
                                expandableState = oldNotificationUiFlagState[item.group.key]?.expandable
                                    ?: false,
                                updateSwipeOffset = {
                                    offsetX = it
                                },
                                type = Old,
                            )
                        }
                    }
                }
            }
        }
    }
}
