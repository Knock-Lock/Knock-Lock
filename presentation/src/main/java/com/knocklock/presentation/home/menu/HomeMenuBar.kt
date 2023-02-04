package com.knocklock.presentation.home.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HomeMenuBar(
    modifier: Modifier = Modifier,
    menuList: ImmutableList<HomeMenu>,
    onClickHomeMenu: (HomeMenu) -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = Color.Black.copy(alpha = 0.5f))
            .then(modifier),
        horizontalArrangement = Arrangement.End
    ) {
        menuList.forEach { homeMenu ->
            HomeMenuItem(
                modifier = Modifier.clickable { onClickHomeMenu(homeMenu) }
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                ,
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
    Text(
        modifier = modifier,
        text = stringResource(id = homeMenu.textRes),
        color = Color.White
    )
}

@Preview
@Composable
fun HomeMenuBarPrev() {
    HomeMenuBar(
        menuList = HomeMenu.values().toList().toImmutableList()
    ) {
    }
}