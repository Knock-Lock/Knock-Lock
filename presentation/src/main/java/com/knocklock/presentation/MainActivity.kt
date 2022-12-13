package com.knocklock.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import com.knocklock.presentation.lockscreen.StartApplicationService
import com.knocklock.presentation.ui.setting.SettingScreen
import com.knocklock.presentation.ui.setting.UserSettings
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService()

        if (!permissionGranted()) {
            val intent = Intent(
                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
            )
            startActivity(intent)
        }

        setContent {
            KnockLockTheme {
                SettingScreen(
                    onBackPressedIconSelected = { /*TODO*/ },
                    onMenuSelected = { /*TODO*/ },
                    onChangedPasswordActivated = { },
                    userSettings = UserSettings(false)
                )
            }
        }
    }
    private fun permissionGranted(): Boolean {
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        return sets.contains(packageName)
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, StartApplicationService::class.java))
        } else {
            startService(Intent(this, StartApplicationService::class.java))
        }
    }
}
