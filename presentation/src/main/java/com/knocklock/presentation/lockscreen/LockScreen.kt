package com.knocklock.presentation.lockscreen

import android.widget.TextClock
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.knocklock.presentation.lockscreen.util.FractionalThreshold
import com.knocklock.presentation.lockscreen.util.rememberSwipeableState
import com.knocklock.presentation.lockscreen.util.swipeable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:01 PM
 */

@Composable
fun LockScreenRoute(
    notificationUiState: NotificationUiState,
    userSwipe: () -> Unit
) {
    LockScreen(notificationUiState = notificationUiState, userSwipe = userSwipe)
}

@Composable
fun LockScreen(
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState,
    userSwipe: () -> Unit
) {
    var startTransitionState by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().background(color = Color(0xFF3629AF))
    ) {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)) // Image로 추후 변경
        Spacer(modifier = Modifier.height(50.dp))
        TextClockComposable(modifier = Modifier.align(Alignment.CenterHorizontally))
        Box(modifier = Modifier.fillMaxSize()) {
            when (notificationUiState) {
                is NotificationUiState.Success -> {
                    LockScreenNotificationListColumn(
                        notificationList = notificationUiState.notificationList.toImmutableList(),
                        scrollableState = startTransitionState
                    )
                }
                is NotificationUiState.Empty -> {
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp).align(Alignment.BottomCenter)
            ) {
                UnLockSwipeBar(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    width = 200.dp,
                    height = 100.dp,
                    userSwipe = userSwipe,
                    startTransitionState = startTransitionState,
                    updateTransitionState = { state ->
                        startTransitionState = state
                    }
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
    startTransitionState: Boolean,
    updateTransitionState: (Boolean) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { height.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)

    val infiniteTransition = rememberInfiniteTransition()
    val movingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (startTransitionState) height.value / 3 else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(swipeableState.targetValue) {
        if (swipeableState.targetValue == 1) {
            userSwipe()
        }
    }
    LaunchedEffect(swipeableState.progress.fraction) {
        updateTransitionState(swipeableState.progress.fraction == 1f)
    }

    Box(
        modifier = modifier.fillMaxWidth() // touch 영역
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                reverseDirection = true,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                Modifier.offset { IntOffset(0, -swipeableState.offset.value.roundToInt()) }
                    .offset { IntOffset(0, movingAnimation.toInt()) }
                    .width(width).height(5.dp)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.padding(bottom = 10.dp))
        }
    }
}

@Composable
fun LockScreenNotificationListColumn(
    modifier: Modifier = Modifier,
    notificationList: ImmutableList<Pair<Triple<String, String, String>, List<Notification>>>,
    scrollableState: Boolean
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(10.dp),
        userScrollEnabled = scrollableState
    ) {
        items(
            items = notificationList,
            key = { item: Pair<Triple<String, String, String>, List<Notification>> -> item.first }
        ) { item: Pair<Triple<String, String, String>, List<Notification>> ->
            GroupLockNotiItem(
                modifier = Modifier.background(color = Color(0xFFFAFAFA).copy(alpha = 0.95f), shape = RoundedCornerShape(10.dp)).clip(
                    RoundedCornerShape(10.dp)
                ),
                notificationList = item.second
            )
        }
    }
}

@Composable
fun TextClockComposable(
    modifier: Modifier = Modifier
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
        modifier = modifier
    )
}
