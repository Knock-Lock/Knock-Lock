package com.knocklock.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.knocklock.presentation.home.menu.HomeMenu

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    homeScreenUiState: HomeScreenUiState,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    HomeScreen(
        modifier = modifier,
        homeScreenUiState = homeScreenUiState,
        onClickHomeMenu = onClickHomeMenu
    )
}