package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import android.widget.TextClock
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.rememberSwipeableState
import com.knocklock.presentation.lockscreen.util.swipeable
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
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState,
    notificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    userSwipe: () -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
    updateNotificationExpandableFlag: (String) -> Unit,
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
) {
    LockScreen(
        modifier = modifier,
        notificationUiState = notificationUiState,
        notificationUiFlagState = notificationUiFlagState,
        userSwipe = userSwipe,
        onRemoveNotification = onRemoveNotification,
        onNotificationClicked = onNotificationClicked,
        updateNotificationExpandableFlag = updateNotificationExpandableFlag,
        updateNotificationClickableFlag = updateNotificationClickableFlag,
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
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (notificationUiState) {
                is NotificationUiState.Success -> {
                    LockScreenNotificationListColumn(
                        groupNotificationList = notificationUiState.groupWithNotification.toImmutableList(),
                        notificationUiFlagState = notificationUiFlagState,
                        onRemoveNotification = onRemoveNotification,
                        onNotificationClicked = onNotificationClicked,
                        updateNotificationExpandableFlag = updateNotificationExpandableFlag,
                        updateNotificationClickableFlag = updateNotificationClickableFlag,
                        notificationHeight = 60.dp,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LockScreenNotificationListColumn(
    notificationHeight: Dp,
    groupNotificationList: ImmutableList<GroupWithNotification>,
    notificationUiFlagState: ImmutableMap<String, NotificationUiFlagState>,
    updateNotificationExpandableFlag: (String) -> Unit,
    updateNotificationClickableFlag: (String, Boolean) -> Unit,
    onRemoveNotification: (List<String>) -> Unit,
    onNotificationClicked: (PendingIntent) -> Unit,
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
        contentPadding = PaddingValues(bottom = 500.dp, start = 10.dp, end = 10.dp),
    ) {
        item {
            TextClockComposable(modifier = Modifier.padding(top = 50.dp))
        }
        groupNotificationList.forEach { item ->

            item(key = item.notifications[0].postedTime) {
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
                        scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == item.notifications[0].postedTime }?.offset ?: Integer.MAX_VALUE
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
                                    enabled = notificationUiFlagState[item.group.key]?.clickable ?: false,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    with(item.group.key) {
                                        if (notificationUiFlagState.containsKey(this)) {
                                            updateNotificationExpandableFlag(this)
                                        }
                                    }
                                }
                                .animateItemPlacement(),
                            onRemoveNotification = onRemoveNotification,
                            notification = item.notifications[0],
                            clickableState = notificationUiFlagState[item.group.key]?.clickable ?: false,
                            expandableState = notificationUiFlagState[item.group.key]?.expandable ?: false,
                            groupNotification = item.notifications.toImmutableList(),
                            onNotificationClicked = onNotificationClicked,
                            updateSwipeOffset = {
                                offsetX = it
                            },
                        )
                    }
                }
            }
            if (notificationUiFlagState.containsKey(item.group.key) && notificationUiFlagState[item.group.key]!!.expandable && item.notifications.size != 1) {
                items(items = item.notifications.drop(1), key = { notification -> notification.postedTime }) { notification ->
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
                            scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == notification.postedTime }?.offset
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
                                onRemoveNotification = {
                                    onRemoveNotification(it)
                                },
                                notification = notification,
                                clickableState = false,
                                expandableState = notificationUiFlagState[item.group.key]?.expandable
                                    ?: false,
                                updateSwipeOffset = {
                                    offsetX = it
                                },
                            )
                        }
                    }
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
