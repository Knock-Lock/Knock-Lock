package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:01 PM
 */

@Composable
fun LockScreenRoute(
    lockScreenViewModel: LockScreenViewModel
) {
    val notificationUiState by lockScreenViewModel.notificationList.collectAsState()
    LockScreen(notificationUiState = notificationUiState)
}

@Composable
fun LockScreen(
    modifier: Modifier = Modifier,
    notificationUiState: NotificationUiState
) {
    Box(
        modifier = modifier.fillMaxSize().background(color = Color(0xFFDADADA))
    ) {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)) // Image로 추후 변경
        when (notificationUiState) {
            is NotificationUiState.Success -> {
                LockScreenNotificationListColumn(
                    notificationList = notificationUiState.notificationList.toImmutableList()
                )
            }
            is NotificationUiState.Empty -> {
            }
        }
    }
}

@Composable
fun LockScreenNotificationListColumn(
    modifier: Modifier = Modifier,
    notificationList: ImmutableList<Notification>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(
            items = notificationList,
            key = { notification -> notification.id }
        ) { notification ->
            LockNotiItem(
                modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                notification = notification
            )
        }
    }
}
