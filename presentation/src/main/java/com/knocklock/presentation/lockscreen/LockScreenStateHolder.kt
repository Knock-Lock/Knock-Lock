package com.knocklock.presentation.lockscreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.knocklock.domain.usecase.notification.DeleteAllNotificationUseCase
import com.knocklock.domain.usecase.notification.GetNotificationUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Created by 김현국 2023/01/03
 * @Time 3:26 PM
 */

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UseCaseEntryPoint {
    fun getNotificationUseCase(): GetNotificationUseCase
    fun deleteAllNotificationUseCase(): DeleteAllNotificationUseCase
}

class LockScreenStateHolder @Inject constructor(
    context: Context,
    private val coroutineScope: CoroutineScope
) {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(NotificationUiState.Empty)
    val notificationList = _notificationList.asStateFlow()

    private val useCaseEntryPoint =
        EntryPointAccessors.fromApplication(
            context,
            UseCaseEntryPoint::class.java
        )

    val getNotificationUseCase = useCaseEntryPoint.getNotificationUseCase()
    val deleteAllNotificationUseCase = useCaseEntryPoint.deleteAllNotificationUseCase()

    init {
        deleteAllNotification()
        getNotification()
    }

    private fun getNotification() {
        coroutineScope.launch {
            getNotificationUseCase().collect { list ->
                val notificationList = list.map {
                    it.toModel()
                }
                _notificationList.value = NotificationUiState.Success(
                    notificationList = notificationList
                )
            }
        }
    }

    private fun deleteAllNotification() {
        coroutineScope.launch {
            deleteAllNotificationUseCase()
        }
    }
}

@Composable
fun rememberLockScreenStateHolder(
    context: Context,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(
    context,
    coroutineScope
) {
    LockScreenStateHolder(context = context, coroutineScope)
}
