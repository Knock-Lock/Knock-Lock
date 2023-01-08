package com.knocklock.presentation.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

/**
 * @Created by 김현국 2022/12/13
 * @Time 4:00 PM
 */

@AndroidEntryPoint
class ScreenEventReceiver(
    private val context: Context,
    private val onScreenEventListener: OnScreenEventListener
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        startLockScreen(context)
    }

    private fun startLockScreen(context: Context?) {
        if (context != null) {
            try {
                onScreenEventListener.openLockScreenByIntent()
            } catch (e: Exception) {
                Log.e("Receiver Exception", e.stackTraceToString())
            }
        }
    }
    fun unregisterReceiver() {
        context.unregisterReceiver(this)
    }

    fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }
        context.registerReceiver(this, intentFilter)
    }
}
interface OnScreenEventListener {
    fun openLockScreenByIntent()
}
