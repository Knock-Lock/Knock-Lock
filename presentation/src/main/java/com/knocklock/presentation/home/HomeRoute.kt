package com.knocklock.presentation.home

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.home.editcontent.HomeEditContentDialog
import com.knocklock.presentation.home.editcontent.HomeEditTimeFormatDialog
import com.knocklock.presentation.home.editcontent.HomeEditType
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.util.getGalleryIntent

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onClickSetting: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    var isShowHomeEditContentDialog by remember { mutableStateOf(false) }
    var isShowHomeEditTimeFormatDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.setWallPaper(uri.toString())
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
                        isShowHomeEditContentDialog = true
                    }
                    HomeMenu.SAVE -> {
                        viewModel.saveLockScreen()
                    }
                    HomeMenu.CLEAR -> {
                    }
                }
            }
        )

        if (isShowHomeEditContentDialog) {
            HomeEditContentDialog(
                modifier = Modifier.fillMaxWidth(),
                clickListener = { editType ->
                    when (editType) {
                        HomeEditType.BACKGROUND -> {
                            galleryLauncher.launch(getGalleryIntent())
                        }

                        HomeEditType.TimeFormat -> {
                            isShowHomeEditTimeFormatDialog = true
                        }

                        else -> {}
                    }
                },
                onDismiss = { isShowHomeEditContentDialog = false }
            )
        }

        if (isShowHomeEditTimeFormatDialog) {
            HomeEditTimeFormatDialog(
                modifier = Modifier.fillMaxWidth(),
                selectedTimeFormat = (homeScreenUiState as? HomeScreenUiState.Success)?.lockScreen?.timeFormat,
                clickListener = viewModel::setTimeFormat,
                onDismiss = { isShowHomeEditTimeFormatDialog = false }
            )
        }
    }
}