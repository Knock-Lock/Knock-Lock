package com.knocklock.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.home.menu.HomeMenuBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenUiState: HomeScreenUiState,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    Box(modifier = modifier) {
        HomeMenuBar(
            modifier = Modifier.align(Alignment.TopEnd),
            menuList = homeScreenUiState.menuList,
            onClickHomeMenu = onClickHomeMenu
        )
    }
}