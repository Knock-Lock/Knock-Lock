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
import com.knocklock.presentation.lockscreen.LockNotiItem
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.ui.theme.KnockLockTheme
import com.knocklock.presentation.widget.ClockWidget
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HomeScreen(
    homeScreenUiState: HomeScreenUiState,
    onHomeMenuClick: (HomeMenu) -> Unit,
    modifier: Modifier = Modifier,
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
                onHomeMenuClick = onHomeMenuClick,
            )
            HomeContent(
                modifier = Modifier.fillMaxSize(),
                homeScreenUiState = homeScreenUiState,
            )
        }
    }
}

@Composable
fun HomeContent(
    homeScreenUiState: HomeScreenUiState.Success,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        HomeBackground(
            scale = HomePreviewScale,
            modifier = modifier,
            lockScreenBackground = homeScreenUiState.lockScreen.background,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scale(HomePreviewScale),
        ) {
            ClockWidget(
                modifier = Modifier
                    .scale(HomePreviewScale)
                    .padding(top = 80.dp)
                    .align(CenterHorizontally),
                timeFormat = homeScreenUiState.lockScreen.timeFormat,
            )
            HomeSampleNotifications(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
        }
    }
}

@Composable
fun HomeSampleNotifications(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        (0..4).forEach { _ ->
            LockNotiItem(
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                    ),
                notification = Notification.default,
                clickableState = false,
                expandableState = false,
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
                    menuList = emptyList<HomeMenu>().toImmutableList(),
                ),
            )
        }
    }
}

private const val HomePreviewScale = 0.75f
