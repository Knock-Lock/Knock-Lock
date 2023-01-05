package com.knocklock.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.knocklock.presentation.lockscreen.service.LockScreenNotificationListener
import com.knocklock.presentation.lockscreen.util.DismissStatusBarService
import com.knocklock.presentation.ui.setting.SettingRoute
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()

        setContent {
            KnockLockTheme {
                SettingRoute(
                    onMenuSelected = { /*TODO*/ },
                    onBackPressedIconSelected = { /*TODO*/ }
                )
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        Toast.makeText(this@MainActivity, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                        if (checkNotificationPermission()) {
                            startService()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        Toast.makeText(this@MainActivity, "권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    }
                })
                .setDeniedMessage("잠금 화면 스크린 사용을 위한 권한을 허용해주세요")
                .setPermissions(
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ).check()
        } else {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        Toast.makeText(this@MainActivity, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                        if (checkNotificationPermission()) {
                            startService()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        Toast.makeText(this@MainActivity, "권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ).check()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isAccessServiceEnabled(this, DismissStatusBarService::class.java)) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        // Todo : Notification 권한 체크 로직 추가예정
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (!sets.contains(packageName)) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            )
            startActivity(intent)
        }
        return sets.contains(packageName)
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LockScreenNotificationListener::class.java))
        } else {
            startService(Intent(this, LockScreenNotificationListener::class.java))
        }
    }
    private fun isAccessServiceEnabled(context: Context, accessibilityServiceClass: Class<*>): Boolean {
        val prefString = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return prefString != null && prefString.contains(context.packageName + "/" + accessibilityServiceClass.name)
    }
}
