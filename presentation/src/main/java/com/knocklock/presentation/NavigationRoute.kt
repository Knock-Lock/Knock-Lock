package com.knocklock.presentation

sealed class NavigationRoute {
    abstract val route: String

    object SettingGraph : NavigationRoute() {
        override val route: String = "setting_graph"

        object Setting : Direction {
            override val route: String = "setting"
        }

        object Password : Direction {
            override val route: String = "password"
        }

        object Credit : Direction {
            override val route: String = "credit"
        }
    }
}

interface Direction {
    val route: String
}