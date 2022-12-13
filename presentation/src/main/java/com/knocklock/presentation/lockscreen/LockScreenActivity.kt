package com.knocklock.presentation.lockscreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * @Created by 김현국 2022/12/13
 * @Time 4:02 PM
 */

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showWhenLockedAndTurnScreenOn()
        val factory = LockScreenFactory(this)
        val lockScreenViewModel = ViewModelProvider(this, factory).get(LockScreenViewModel::class.java)
        setContent {
            KnockLockTheme {
                LockScreenRoute(lockScreenViewModel)
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
    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}

class LockScreenFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LockScreenViewModel::class.java)) {
            return LockScreenViewModel(context) as T
        }
        throw IllegalArgumentException("Not found ViewModel Class")
    }
}
