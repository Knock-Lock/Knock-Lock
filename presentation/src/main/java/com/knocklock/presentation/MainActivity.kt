package com.knocklock.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.knocklock.presentation.lockscreen.LockNotiItem
import com.knocklock.presentation.lockscreen.LockScreen
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.ui.theme.KnockLockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KnockLockTheme {
                LockScreenRoute()
            }
        }
    }
}
