package com.knocklock.presentation.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager

/**
 * @Created by 김현국 2023/06/22
 */
class PhoneCallStateReceiver(
    private val context: Context,
    private val callStateCallBack: CallStateCallBack,
) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        try {
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                callStateCallBack.onReceiveCallState(CallState.RINGING)
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                callStateCallBack.onReceiveCallState(CallState.OFFHOOK)
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                callStateCallBack.onReceiveCallState(CallState.IDLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun unregisterReceiver() {
        context.unregisterReceiver(this)
    }

    fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(callIntent)
        }
        context.registerReceiver(this, filter)
    }
}

interface CallStateCallBack {
    fun onReceiveCallState(callState: CallState)
}
enum class CallState {
    RINGING, OFFHOOK, IDLE
}
const val callIntent = "android.intent.action.PHONE_STATE"
