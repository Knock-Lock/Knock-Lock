package com.knocklock.presentation.lockscreen

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.knocklock.domain.model.User
import com.knocklock.domain.usecase.setting.GetUserUseCase
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
import javax.inject.Inject

/**
 * @Created by 김현국 2023/01/03
 * @Time 3:26 PM
 */

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UseCaseEntryPoint {
    fun getUserUseCase(): GetUserUseCase
}

@Stable
class LockScreenStateHolder @Inject constructor(
    context: Context,
    private val scope: CoroutineScope
) {
    private val useCaseEntryPoint = EntryPointAccessors.fromApplication(
        context,
        UseCaseEntryPoint::class.java
    )

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val _currentLockState: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentLockState = _currentLockState.asStateFlow()

    private val packageManager by lazy { context.packageManager }

    init {
        getCurrentLockState()
    }

    fun updateNotificationList(notificationList: List<StatusBarNotification>) {
        val notificationUiState = NotificationUiState.Success(
            notificationList = notificationList.asSequence()
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
                        val date = Date(statusBarNotification.postTime)
                        val stringPostTime = try {
                            SimpleDateFormat("a HH:mm", Locale.KOREA).format(date)
                        } catch (e: Exception) {
                            ""
                        }
                        val applicationInfo: ApplicationInfo?

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            applicationInfo = runCatching {
                                packageManager.getApplicationInfo(
                                    packageName,
                                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                                )
                            }.onSuccess { info ->
                                appTitle = packageManager.getApplicationLabel(info).toString()
                            }.getOrNull()
                        } else {
                            applicationInfo = runCatching {
                                packageManager.getApplicationInfo(packageName, 0)
                            }.onSuccess { info ->
                                appTitle = packageManager.getApplicationLabel(info).toString()
                            }.getOrNull()
                        }

                        val icon: Drawable? = if (applicationInfo != null) {
                            packageManager.getApplicationIcon(applicationInfo)
                        } else null

                        Notification(
                            id = statusBarNotification.key,
                            drawable = icon,
                            appTitle = if (subText == "") appTitle else subText,
                            notiTime = stringPostTime,
                            title = title,
                            isClearable = statusBarNotification.isClearable,
                            content = content
                        )
                    }
                }.groupBy { notification ->
                    GroupKey(
                        packageName = notification.id.split("|")[1],
                        appTitle = notification.appTitle,
                        title = notification.title
                    )
                }
                .map { entry ->
                    GroupNotification(
                        entry.toPair()
                    )
                }.sortedByDescending { groupNotification ->
                    groupNotification.notifications.second.first().notiTime
                }
        )
        _notificationList.value = notificationUiState
    }
    private fun convertString(var1: Any?): String {
        return var1?.toString() ?: ""
    }

    private fun getCurrentLockState() {
        scope.launch {
            useCaseEntryPoint.getUserUseCase().invoke().collect { user ->
                _currentLockState.value = user
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
