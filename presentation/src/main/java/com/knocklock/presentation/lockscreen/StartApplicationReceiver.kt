package com.knocklock.presentation.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @Created by 김현국 2022/12/13
 * @Time 4:00 PM
 */
class StartApplicationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        startLockScreen(context)
    }

    private fun startLockScreen(context: Context?) {
        if (context != null) {
            try {
                val intent = Intent(context, LockScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("Receiver Exception", e.stackTraceToString())
            }
        }
    }
}
