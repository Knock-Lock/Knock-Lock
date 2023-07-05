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
                callStateCallBack.onReceiveCallState(CallState.Ringing)
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                callStateCallBack.onReceiveCallState(CallState.OffHook)
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                callStateCallBack.onReceiveCallState(CallState.Idle)
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
            addAction(CallIntent)
        }
        context.registerReceiver(this, filter)
    }
}

interface CallStateCallBack {
    fun onReceiveCallState(callState: CallState)
}
enum class CallState {
    Ringing, OffHook, Idle
}
const val CallIntent = "android.intent.action.PHONE_STATE"
