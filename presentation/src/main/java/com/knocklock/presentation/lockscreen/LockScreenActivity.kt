package com.knocklock.presentation.lockscreen

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Method

/**
 * @Created by 김현국 2022/12/13
 * @Time 4:02 PM
 */

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {
    private val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableStatusBar()
        hideSystemUI()
        setContent {
            KnockLockTheme {
                LockScreenRoute()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                finish()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        window.statusBarColor = Color.TRANSPARENT
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    private fun disableWithReflection() {
        val sbservice = TedPermissionProvider.context.getSystemService("statusbar")
        try {
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            var collapseStatusBar: Method? = null
            collapseStatusBar = if (Build.VERSION.SDK_INT <= 16) {
                statusbarManager.getMethod("collapse")
            } else {
                statusbarManager.getMethod("collapsePanels")
            }
            collapseStatusBar.isAccessible = true
            collapseStatusBar.invoke(sbservice)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun disableStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        } else {
            disableWithReflection()
        }
    }
}
