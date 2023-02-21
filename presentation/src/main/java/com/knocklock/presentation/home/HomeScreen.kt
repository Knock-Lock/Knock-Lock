package com.knocklock.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.R
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.home.menu.HomeMenuBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenUiState: HomeScreenUiState,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    when (homeScreenUiState) {
        HomeScreenUiState.Loading,
        -> Unit
        is HomeScreenUiState.Success -> {
            Box(modifier = modifier.navigationBarsPadding()) {
                HomeMenuBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .align(Alignment.TopEnd),
                    menuList = homeScreenUiState.menuList,
                    onClickHomeMenu = onClickHomeMenu
                )
            }
            val painter =
                if (homeScreenUiState.lockScreen.background is LockScreenBackground.DefaultWallPaper) {
                    painterResource(id = R.drawable.default_wallpaper)
                } else {
                    painterResource(id = (homeScreenUiState.lockScreen.background as LockScreenBackground.LocalImage).imageRes)
                }
            Image(
                modifier = modifier,
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alpha = 0.4f
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    lockScreen: LockScreen
) {
    Box(modifier = modifier) {
        // implement LockScreen
    }
}