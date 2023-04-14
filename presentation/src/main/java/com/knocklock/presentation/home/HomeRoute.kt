package com.knocklock.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.home.editcontent.HomeEditContentDialog
import com.knocklock.presentation.home.menu.HomeMenu
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onClickSetting: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    var isShowHomeEditContent by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        HomeScreen(
            modifier = modifier,
            homeScreenUiState = homeScreenUiState,
            onClickHomeMenu = { homeMenu ->
                when (homeMenu) {
                    HomeMenu.SETTING -> {
                        onClickSetting()
                    }
                    HomeMenu.EDIT -> {
                        isShowHomeEditContent = true
                    }
                    HomeMenu.SAVE -> {
                        //vm.saveWallPaper()
                    }
                    HomeMenu.CLEAR -> {
                    }
                }
            }
        )

        if (isShowHomeEditContent) {
            HomeEditContentDialog(
                clickListener = {},
                onDismiss = { isShowHomeEditContent = false }
            )
        }
    }
}