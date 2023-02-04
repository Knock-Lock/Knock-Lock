package com.knocklock.presentation.home

import com.knocklock.presentation.home.menu.HomeMenu
import kotlinx.collections.immutable.ImmutableList

data class HomeScreenUiState(
    val menuList: ImmutableList<HomeMenu>
)