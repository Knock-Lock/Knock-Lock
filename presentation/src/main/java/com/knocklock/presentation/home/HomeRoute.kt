package com.knocklock.presentation.home

import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.navigation.NavigationRoute
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onClickSetting: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val launcherIntent = Intent(
        Intent.ACTION_GET_CONTENT,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    ).apply {
        type = "image/*"
        action = Intent.ACTION_OPEN_DOCUMENT
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

    }
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            //TODO Implement Sheet Content
            Spacer(modifier = Modifier.fillMaxSize())
        },
        sheetBackgroundColor = Color.Black.copy(alpha = 0.8f)
    ) {
        HomeScreen(
            modifier = modifier,
            homeScreenUiState = homeScreenUiState,
            onClickHomeMenu = { homeMenu ->
                when (homeMenu) {
                    HomeMenu.SETTING -> {
                        onClickSetting()
                    }
                    HomeMenu.EDIT -> {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }
                    HomeMenu.SAVE -> {
                        //vm.saveWallPaper()
                    }
                    HomeMenu.CLEAR -> {
                    }
                }
            }
        )
    }
}