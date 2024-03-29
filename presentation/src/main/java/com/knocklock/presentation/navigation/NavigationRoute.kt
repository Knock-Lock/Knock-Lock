package com.knocklock.presentation.navigation

sealed class NavigationRoute {
    abstract val route: String

    object HomeGraph : NavigationRoute() {
        override val route: String = "home_graph"

        object Home : Direction {
            override val route: String = "home"
        }
    }

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