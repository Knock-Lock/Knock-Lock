package com.knocklock.presentation

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.knocklock.presentation.lockscreen.service.LockScreenNotificationListener
import com.knocklock.presentation.navigation.KnockLockNavHost
import com.knocklock.presentation.ui.theme.KnockLockTheme
import com.knocklock.presentation.util.showShortToastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestPermission()

        setContent {
            KnockLockTheme {
                val navController = rememberNavController()
                KnockLockNavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        showShortToastMessage("권한이 허용되었습니다.")
                        if (checkNotificationPermission()) {
                            startService()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        showShortToastMessage("권한을 허용해주세요.")
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
                        showShortToastMessage("권한이 허용되었습니다.")
                        if (checkNotificationPermission()) {
                            startService()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        showShortToastMessage("권한이 허용되지 않았습니다.")
                    }
                })
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ).check()
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

    companion object {
        const val GITHUB_LINK = "https://github.com/Knock-Lock/Knock-Lock"
        const val KNOCK_LOCK_ACCOUNT = "100056653114"
        const val KNOCK_LOCK_EMAIL_ADDRESS = "rldkvos2@gmail.com"
    }
}
