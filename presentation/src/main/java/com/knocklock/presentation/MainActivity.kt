package com.knocklock.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.lockscreen.StartApplicationService
import com.knocklock.presentation.ui.setting.SettingRoute
import com.knocklock.presentation.ui.setting.SettingViewModel
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService()
        checkPermissionGranted()

        setContent {
            val settingViewModel : SettingViewModel = hiltViewModel()
            KnockLockTheme {
                SettingRoute(
                    viewModel = settingViewModel,
                    onMenuSelected = { /*TODO*/ },
                    onBackPressedIconSelected = { /*TODO*/ }
                )
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        return sets.contains(packageName)
    }

    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun checkPermissionGranted() {
        if (!checkNotificationPermission()) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            )
            startActivity(intent)
        }
        if (!checkOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + this.packageName)
            )

            val activityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    when (result.resultCode) {
                        RESULT_OK -> {
                        }
                    }
                }
            activityResultLauncher.launch(intent)
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, StartApplicationService::class.java))
        } else {
            startService(Intent(this, StartApplicationService::class.java))
        }
    }
}
