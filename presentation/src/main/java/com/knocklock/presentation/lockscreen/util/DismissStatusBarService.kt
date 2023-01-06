package com.knocklock.presentation.lockscreen.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.view.accessibility.AccessibilityEvent

/**
 * @Created by 김현국 2022/12/20
 * @Time 3:51 PM
 */
class DismissStatusBarService : AccessibilityService() {

    private val info by lazy { AccessibilityServiceInfo() }
    override fun onServiceConnected() {
        super.onServiceConnected()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.notificationTimeout = 100
        serviceInfo = info
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        dismissNotificationShade()
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()
        disableSelf()
    }

    private fun dismissNotificationShade(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
