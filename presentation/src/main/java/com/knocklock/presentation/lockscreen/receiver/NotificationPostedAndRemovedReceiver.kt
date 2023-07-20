package com.knocklock.presentation.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * @Created by 김현국 2023/05/06
 */
class NotificationPostedAndRemovedReceiver(
    private val context: Context,
    private val onPostedNotificationPostedListener: NotificationPostedListener,
) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        intent?.getStringExtra(PostedNotification)?.let { data ->
            onPostedNotificationPostedListener.onPostedNotification(data)
        }
        intent?.getStringExtra(RemovedNotification)?.let { key ->
            onPostedNotificationPostedListener.onRemovedNotifications(key)
        }
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(this)
    }

    fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(PostAndRemove)
        }
        context.registerReceiver(this, filter)
    }

    companion object {
        const val PostedNotification = "PostedNotification"
        const val RemovedNotification = "RemovedNotification"
        const val PostAndRemove = "com.knocklock.presentation.lockscreen.receiver"
    }
}

interface NotificationPostedListener {
    fun onPostedNotification(notification: String)
    fun onRemovedNotifications(key: String)
}
