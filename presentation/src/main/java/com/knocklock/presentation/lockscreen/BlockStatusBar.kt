package com.knocklock.presentation.lockscreen

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @Created by 김현국 2022/12/22
 * @Time 5:31 PM
 */
class BlockStatusBar constructor(
    context: Context,
    isPaused: Boolean
) {
    private var context: Context

// To keep track of activity's window focus
    private var currentFocus: Boolean = false

// To keep track of activity's foreground/background status
    private var isPaused: Boolean = false

    var collapseNotificationHandler: Handler? = null
    lateinit var collapseStatusBar: Method

    init {
        this.context = context
        this.isPaused = isPaused
        collapseNow()
    }

    fun collapseNow() {
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = Handler(Looper.getMainLooper())
        }

        /*
        창 포커스가 손신될 경우 && 활동이 pause 상태가 아닌경우
        알림 패널을 표시하면 현재 액티비티 창에서 포커스를 훔치지만, pause상태가 되지않기때문에
        위와 같이 검사합니다.
         */

        if (!currentFocus && !isPaused) {
            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler!!.postDelayed(
                object : Runnable {
                    override fun run() {
                        // statusBar manager를 사용하기 위한 reflection

                        val statusBarService = context.getSystemService("statusbar")
                        val statusbarManager = Class.forName("android.app.StatusBarManager")

                        try {
                            collapseStatusBar = statusbarManager.getMethod("collapsePanels")
                        } catch (e: NoSuchMethodException) {
                            e.printStackTrace()
                        }

                        collapseStatusBar.isAccessible = true

                        try {
                            collapseStatusBar.invoke(statusBarService)
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        } catch (e: IllegalAccessException) {
                            e.printStackTrace()
                        } catch (e: InvocationTargetException) {
                            e.printStackTrace()
                        }

                        // 알림창 닫기를 했지만, 포커스가 아직 돌아오지 않은경우 다시 호출합니다.
                        if (!currentFocus && !isPaused) {
                            collapseNotificationHandler!!.postDelayed(this, 50L)
                        }

                        if (!currentFocus && isPaused) {
                            // postDelayed로 작성한 위의 로직을 제거합니다.
                            collapseNotificationHandler!!.removeCallbacksAndMessages(null)
                        }
                    }
                },
                1L
            )
        }
    }
}
