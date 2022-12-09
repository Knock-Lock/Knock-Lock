package com.knocklock.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.lockscreen.LockScreenViewModel
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

        val factory = LockScreenFactory(this)
        val lockScreenViewModel = ViewModelProvider(this, factory).get(LockScreenViewModel::class.java)
        setContent {
            KnockLockTheme {
                LockScreenRoute(lockScreenViewModel)
            }
        }
    }
    private fun permissionGranted(): Boolean {
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        return sets.contains(packageName)
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
