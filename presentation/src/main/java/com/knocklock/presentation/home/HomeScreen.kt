package com.knocklock.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.R
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
    val imagePainter = when (homeScreenUiState.lockScreen.background) {
        is LockScreenBackground.DefaultWallPaper -> {
            rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(R.drawable.default_wallpaper)
                    .allowHardware(false)
                    .build()
            )
        }
        is LockScreenBackground.LocalImage -> {
            rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data((homeScreenUiState.lockScreen.background as LockScreenBackground.LocalImage).imageUri)
                    .allowHardware(false)
                    .build()
            )
        }
    }

    val palette = (imagePainter.state as? AsyncImagePainter.State.Success)?.let {
        Palette.from((it.result.drawable.toBitmap())).generate()
    }

    val backgroundColor = palette?.mutedSwatch?.let { Color(it.rgb) } ?: Color.Transparent

    Box(
        modifier = modifier
            .background(color = backgroundColor)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .align(Alignment.Center),
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.4f
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
