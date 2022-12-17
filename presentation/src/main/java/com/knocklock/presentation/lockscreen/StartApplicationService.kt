package com.knocklock.presentation.lockscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * @Created by 김현국 2022/12/13
 * @Time 4:09 PM
 */
class StartApplicationService : Service() {

    private val receiver by lazy { StartApplicationReceiver() }
    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    companion object {
        private const val ANDROID_CHANNEL_ID = "KnockLockScreenNotification"
        private const val ANDROID_CHANNEL_NAME = "KnockLockScreen"
    }
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(9999, createNotification())

        return START_REDELIVER_INTENT
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            ANDROID_CHANNEL_ID,
            ANDROID_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val contentIntent = Intent(this, LockScreenActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
            .setContentTitle("contentTitle")
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
