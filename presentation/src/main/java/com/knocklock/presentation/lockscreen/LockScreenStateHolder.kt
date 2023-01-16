package com.knocklock.presentation.lockscreen

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

/**
 * @Created by 김현국 2023/01/03
 * @Time 3:26 PM
 */

class LockScreenStateHolder @Inject constructor(
    context: Context,
    val coroutineScope: CoroutineScope
) {

    private val date by lazy { Date() }

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val packageManager by lazy { context.packageManager }

    fun updateNotificationArray(notificationArray: Array<StatusBarNotification>) {
        val notificationUiState = NotificationUiState.Success(
            notificationList = notificationArray.map { statusBarNotification ->

                with(statusBarNotification.notification.extras) {
                    var appTitle = ""

                    val subText = getString(android.app.Notification.EXTRA_SUB_TEXT).toString() ?: ""
                    val title = getString(android.app.Notification.EXTRA_TITLE).toString() ?: ""
                    val content = getString(android.app.Notification.EXTRA_TEXT).toString() ?: ""
                    val packageName = statusBarNotification.packageName

                    val iconId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        statusBarNotification.notification.smallIcon.resId
                    } else {
                        getInt(android.app.Notification.EXTRA_SMALL_ICON)
                    }
                    val applicationInfo: ApplicationInfo?

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        applicationInfo = try {
                            packageManager.getApplicationInfo(
                                packageName,
                                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                            )
                        } catch (e: PackageManager.NameNotFoundException) {
                            null
                        }
                        if (applicationInfo == null) {
                            println("package name : unknown")
                        } else {
                            val appName = packageManager.getApplicationLabel(applicationInfo)
                            appTitle = appName.toString()
                        }
                    } else {
                        applicationInfo = try {
                            packageManager.getApplicationInfo(packageName, 0)
                        } catch (e: PackageManager.NameNotFoundException) {
                            null
                        }
                        if (applicationInfo == null) {
                            println("package name : unknown")
                        } else {
                            val appName = packageManager.getApplicationLabel(applicationInfo)
                            appTitle = appName.toString()
                        }
                    }

                    val icon: Drawable? = if (applicationInfo != null) {
                        packageManager.getApplicationIcon(applicationInfo)
                    } else null

                    Notification(
                        id = statusBarNotification.key.hashCode(),
                        drawable = icon,
                        appTitle = if (subText == "null") appTitle else subText, // Bold title
                        notiTime = "",
                        title = title,
                        content = content
                    )
                }
            }
        )
        _notificationList.value = notificationUiState
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
