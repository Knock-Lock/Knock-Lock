package com.knocklock.presentation.lockscreen

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils

/**
 * @Created by 김현국 2022/12/04
 * @Time 5:50 PM
 */
class LockScreenNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName
        if (sbn != null && !TextUtils.isEmpty(packageName)) {
            val notification: Notification = sbn.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE)
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)
            val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
            val smallIcon = notification.smallIcon
            val largeIcon = notification.getLargeIcon()

            println(smallIcon)

            println("title : $title")
            println("text: $text")
            println("subText: $subText")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
