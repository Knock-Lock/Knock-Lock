package com.knocklock.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.home.menu.HomeMenuBar
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenUiState: HomeScreenUiState,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    Box(modifier = modifier.navigationBarsPadding()) {
        if (homeScreenUiState is HomeScreenUiState.Success) {
            HomeMenuBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
                menuList = homeScreenUiState.menuList,
                onClickHomeMenu = onClickHomeMenu
            )
            HomeContent(
                modifier = Modifier.fillMaxSize(),
                homeScreenUiState = homeScreenUiState
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    homeScreenUiState: HomeScreenUiState.Success,
) {
    Box(modifier = modifier) {
        HomeBackground(
            modifier = modifier,
            lockScreenBackground = homeScreenUiState.lockScreen.background
        )
    }
}

@Preview
@Composable
fun HomeContentPrev() {
    KnockLockTheme {
        Surface {
            HomeContent(
                modifier = Modifier.fillMaxSize(),
                homeScreenUiState = HomeScreenUiState.Success(
                    lockScreen = LockScreen(LockScreenBackground.DefaultWallPaper),
                    menuList = emptyList<HomeMenu>().toImmutableList()
                )
            )
        }
    }
}
