package com.knocklock.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.home.menu.HomeMenu

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onClickHomeMenu: (HomeMenu) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    HomeScreen(
        modifier = modifier,
        homeScreenUiState = homeScreenUiState,
        onClickHomeMenu = onClickHomeMenu
    )
}