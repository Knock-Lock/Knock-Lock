package com.knocklock.presentation.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * @Created by 김현국 2022/12/28
 * @Time 2:47 PM
 */
class SystemBarEventReceiver(
    private val context: Context,
    private val intentFilter: IntentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS),
    private val onSystemBarEventListener: OnSystemBarEventListener
) : BroadcastReceiver() {

    fun registerReceiver() {
        context.registerReceiver(this, intentFilter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(this)
    }

    override fun onReceive(p0: Context?, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
            if (reason != null) {
                if (reason == SYSTEM_DIALOG_REASON_HOME_KEY || reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                    onSystemBarEventListener.onSystemBarClicked()
                }
            }
        }
    }

    companion object {
        const val SYSTEM_DIALOG_REASON_KEY = "reason"
        const val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
    }
}
interface OnSystemBarEventListener {
    fun onSystemBarClicked()
}
