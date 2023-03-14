package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import android.content.Context
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.knocklock.domain.model.Group
import com.knocklock.domain.model.GroupWithNotification
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.domain.model.Notification
import com.knocklock.domain.model.User
import com.knocklock.domain.repository.NotificationRepository
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.setting.GetUserUseCase
import com.knocklock.presentation.lockscreen.mapper.convertString
import com.knocklock.presentation.lockscreen.mapper.toModel
import com.knocklock.presentation.lockscreen.model.toModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Created by 김현국 2023/01/03
 * @Time 3:26 PM
 */

@Stable
class LockScreenStateHolder(
    context: Context,
    private val scope: CoroutineScope
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UseCaseEntryPoint {
        fun getUserUseCase(): GetUserUseCase
        fun getLockScreenUseCase(): GetLockScreenUseCase
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RepositoryEntryPoint {
        fun getNotificationRepository(): NotificationRepository
    }

    private val useCaseEntryPoint = EntryPointAccessors.fromApplication(
        context,
        UseCaseEntryPoint::class.java
    )

    private val repositoryEntryPoint = EntryPointAccessors.fromApplication(
        context,
        RepositoryEntryPoint::class.java
    )

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val _currentLockState: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentLockState = _currentLockState.asStateFlow()

    private val packageManager by lazy { context.packageManager }

    private val _currentBackground: MutableStateFlow<LockScreen> = MutableStateFlow(LockScreen(LockScreenBackground.DefaultWallPaper))
    val currentBackground = _currentBackground.asStateFlow()

    init {
        getCurrentLockState()
        getCurrentLockScreenBackground()
        getGroupNotifications()
    }

    suspend fun updateNotificationList(notificationList: List<StatusBarNotification>) {
        val list = notificationList.asSequence()
            .filter { statusBarNotification ->
                with(statusBarNotification.notification.extras) {
                    val title: String = convertString(getCharSequence("android.title"))
                    val content: String = convertString(getCharSequence("android.text"))
                    title != "" || content != ""
                }
            }.map { statusBarNotification ->
                with(statusBarNotification.notification.extras) {
                    var appTitle = ""
                    val subText: String = convertString(getCharSequence("android.subText"))
                    val title: String = convertString(getCharSequence("android.title"))
                    val content: String = convertString(getCharSequence("android.text"))
                    val packageName = statusBarNotification.packageName

                    Notification(
                        id = statusBarNotification.key,
                        postedTime = statusBarNotification.postTime,
                        appTitle = if (subText == "") appTitle else subText,
                        title = title,
                        isClearable = statusBarNotification.isClearable,
                        content = content,
                        packageName = packageName
                    )
                }
            }.groupBy { notification ->
                GroupKey(
                    packageName = notification.id.split("|")[1],
                    appTitle = notification.appTitle,
                    title = notification.title
                )
            }.map {
                GroupWithNotification(
                    group = Group(key = it.key.toString()),
                    notifications = it.value
                )
            }

        for (i in list) {
            scope.launch {
                repositoryEntryPoint.getNotificationRepository().insertGroup(i.group)
                repositoryEntryPoint.getNotificationRepository().insertNotifications(
                    *i.notifications.toTypedArray()
                )
            }
        }
    }

    private fun getGroupNotifications() {
        scope.launch {
            repositoryEntryPoint.getNotificationRepository().getGroupWithNotificationsWithSorted().collect { groups ->
                _notificationList.value = NotificationUiState.Success(
                    groups.map { it.toModel(packageManager) }
                )
            }
        }
    }

    private fun getCurrentLockState() {
        scope.launch {
            useCaseEntryPoint.getUserUseCase().invoke().collect { user ->
                _currentLockState.value = user
            }
        }
    }

    private fun getCurrentLockScreenBackground() {
        scope.launch {
            useCaseEntryPoint.getLockScreenUseCase().invoke().collect { lockscreen ->
                _currentBackground.value = lockscreen
            }
        }
    }
}

data class GroupKey(
    val packageName: String,
    val appTitle: String,
    val title: String
)

@Composable
fun rememberLockScreenStateHolder(
    context: Context,
    scope: CoroutineScope = rememberCoroutineScope()
) = remember(context, scope) {
    LockScreenStateHolder(context = context, scope)
}
