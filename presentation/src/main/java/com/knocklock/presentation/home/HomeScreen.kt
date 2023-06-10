package com.knocklock.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.home.menu.HomeMenuBar
import com.knocklock.presentation.ui.theme.KnockLockTheme
import com.knocklock.presentation.widget.ClockWidget
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
            scale = homePreviewScale,
            modifier = modifier,
            lockScreenBackground = homeScreenUiState.lockScreen.background
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(homePreviewScale)
        ) {
            ClockWidget(
                modifier = Modifier
                    .scale(homePreviewScale)
                    .padding(top = 80.dp)
                    .align(CenterHorizontally),
                timeFormat = homeScreenUiState.lockScreen.timeFormat
            )
            HomeSampleNotifications(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
fun HomeSampleNotifications(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        (0..4).forEach { _ ->
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        color = Color.Companion.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp)
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun HomeContentPrev() {
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

private const val homePreviewScale = 0.75f