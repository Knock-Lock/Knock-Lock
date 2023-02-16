package com.knocklock.presentation.lockscreen.navigation

/**
 * @Created by 김현국 2023/02/16
 */

sealed class LockScreenNavigationRoute {
    abstract val route: String

    object LockScreenGraph : LockScreenNavigationRoute() {
        override val route: String = "lockscreen_graph"

        object LockScreen : Direction {
            override val route: String = "lock"
        }

        object PassWordScreen : Direction {
            override val route: String = "password"
        }
    }
}

interface Direction {
    val route: String
}
