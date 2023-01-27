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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @Created by 김현국 2023/01/03
 * @Time 3:26 PM
 */

@Stable
class LockScreenStateHolder @Inject constructor(
    context: Context
) {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val packageManager by lazy { context.packageManager }

    fun updateNotificationArray(notificationArray: Array<StatusBarNotification>) {
        val notificationUiState = NotificationUiState.Success(
            notificationList = notificationArray.asSequence()
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
                            content = content
                        )
                    }
                }.groupBy {
                    GroupKey(
                        packageName = it.id.split("|")[1],
                        appTitle = it.appTitle,
                        title = it.title
                    )
                }
                .map {
                    GroupNotification(
                        it.toPair()
                    )
                }
        )
        _notificationList.value = notificationUiState
    }
    private fun convertString(var1: Any?): String {
        return var1?.toString() ?: ""
    }
}

data class GroupKey(
    val packageName: String,
    val appTitle: String,
    val title: String
)

@Composable
fun rememberLockScreenStateHolder(
    context: Context
) = remember(context) {
    LockScreenStateHolder(context = context)
}
