package com.knocklock.presentation.home

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.home.editcontent.HomeEditContentDialog
import com.knocklock.presentation.home.editcontent.HomeEditType
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.util.getGalleryIntent
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onClickSetting: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    var isShowHomeEditContent by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.saveTmpWallPaper(uri.toString())
                }
            }
        }

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
                        viewModel.saveWallPaper()
                    }
                    HomeMenu.CLEAR -> {
                    }
                }
            }
        )

        if (isShowHomeEditContent) {
            HomeEditContentDialog(
                clickListener = { editType ->
                    when (editType) {
                        HomeEditType.BACKGROUND -> {
                            galleryLauncher.launch(getGalleryIntent())
                        }

                        else -> {}
                    }
                    isShowHomeEditContent = false
                },
                onDismiss = { isShowHomeEditContent = false }
            )
        }
    }
}