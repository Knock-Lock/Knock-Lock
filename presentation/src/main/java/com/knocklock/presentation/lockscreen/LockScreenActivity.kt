package com.knocklock.presentation.lockscreen

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
}
