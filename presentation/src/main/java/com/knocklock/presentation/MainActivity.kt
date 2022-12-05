package com.knocklock.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.ui.theme.KnockLockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!permissionGranted()) {
            val intent = Intent(
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
            )
            startActivity(intent)
        }

        setContent {
            KnockLockTheme {
                LockScreenRoute()
            }
        }
    }
    private fun permissionGranted(): Boolean {
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        return sets.contains(packageName)
    }
}
