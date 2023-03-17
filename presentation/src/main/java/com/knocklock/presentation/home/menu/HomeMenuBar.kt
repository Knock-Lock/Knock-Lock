package com.knocklock.presentation.home.menu

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.extenstions.noRippleClickable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HomeMenuBar(
    modifier: Modifier = Modifier,
    menuList: ImmutableList<HomeMenu>,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        menuList.forEach { homeMenu ->
            HomeMenuItem(
                modifier = Modifier
                    .noRippleClickable { onClickHomeMenu(homeMenu) }
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                homeMenu = homeMenu,
            )
        }
    }
}

@Composable
fun HomeMenuItem(
    modifier: Modifier = Modifier,
    homeMenu: HomeMenu
) {
    var size by remember { mutableStateOf(0.2f) }
    val animateScale by animateFloatAsState(
        targetValue = size,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        size = 1.0f
    }

    Box(
        modifier = modifier
            .scale(animateScale)
            .clip(CircleShape)
            .background(color = Color.Black.copy(alpha = 0.4f))
    ) {
        Icon(
            modifier = Modifier
                .padding(all = 8.dp)
                .align(Alignment.Center),
            imageVector = homeMenu.icon,
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun HomeMenuBarPrev() {
    HomeMenuBar(
        menuList = HomeMenu.values().toList().toImmutableList()
    ) {
    }
}