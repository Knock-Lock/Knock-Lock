package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import android.content.pm.ApplicationInfo
import android.service.notification.StatusBarNotification
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.domain.model.User
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.setting.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * @Created by 김현국 2023/03/03
 */

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getLockScreenUseCase: GetLockScreenUseCase
) : ViewModel() {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val _currentLockState: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentLockState = _currentLockState.asStateFlow()

//    private val packageManager by lazy { context.packageManager }

    private val _currentBackground: MutableStateFlow<LockScreen> = MutableStateFlow(
        LockScreen(
            LockScreenBackground.DefaultWallPaper
        )
    )
    val currentBackground = _currentBackground.asStateFlow()

    init {
        getCurrentLockState()
        getCurrentLockScreenBackground()
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
                        val intent: PendingIntent? = statusBarNotification.notification.contentIntent
                        val stringPostTime = try {
                            SimpleDateFormat("a HH:mm", Locale.KOREA).format(date)
                        } catch (e: Exception) {
                            ""
                        }
                        val applicationInfo: ApplicationInfo?

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                            applicationInfo = runCatching {
//                                packageManager.getApplicationInfo(
//                                    packageName,
//                                    PackageManager.ApplicationInfoFlags.of(
//                                        PackageManager.GET_META_DATA.toLong())
//                                )
//                            }.onSuccess { info ->
//                                appTitle = packageManager.getApplicationLabel(info).toString()
//                            }.getOrNull()
//                        } else {
//                            applicationInfo = runCatching {
//                                packageManager.getApplicationInfo(packageName, 0)
//                            }.onSuccess { info ->
//                                appTitle = packageManager.getApplicationLabel(info).toString()
//                            }.getOrNull()
//                        }
//
//                        val icon: Drawable? = if (applicationInfo != null) {
//                            packageManager.getApplicationIcon(applicationInfo)
//                        } else null

                        Notification(
                            id = statusBarNotification.key,
                            drawable = null,
                            packageName = packageName,
                            appTitle = if (subText == "") appTitle else subText,
                            notiTime = stringPostTime,
                            title = title,
                            isClearable = statusBarNotification.isClearable,
                            content = content,
                            intent = intent
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
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _currentLockState.value = user
            }
        }
    }

    private fun getCurrentLockScreenBackground() {
        viewModelScope.launch {
            getLockScreenUseCase().collect { lockscreen ->
                _currentBackground.value = lockscreen
            }
        }
    }

//    data class GroupKey(
//        val packageName: String,
//        val appTitle: String,
//        val title: String
//    )
}
