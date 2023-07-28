package com.knocklock.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.knocklock.presentation.home.editcontent.HomeEditContentDialog
import com.knocklock.presentation.home.editcontent.HomeEditTimeFormatDialog
import com.knocklock.presentation.home.editcontent.HomeEditType
import com.knocklock.presentation.home.menu.HomeMenu
import java.io.File
import java.util.Objects

@Composable
fun HomeRoute(
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsState(HomeScreenUiState.Loading)

    var isShowHomeEditContentDialog by remember { mutableStateOf(false) }
    var isShowHomeEditTimeFormatDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    if (context == null) {
        return
    }

    val screenX = LocalConfiguration.current.screenWidthDp
    val screenY = LocalConfiguration.current.screenHeightDp

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            viewModel.setWallPaper(imageUri.toString())
        } else {
            val exception = result.error
            // 후에 토스트 추가
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { photoUri ->
            photoUri?.let { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            val file = File.createTempFile("IMG_", ".jpg", context.filesDir)

            val fileUri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                "com.knocklock.provider",
                file,
            )
            val cropOptions = CropImageContractOptions(
                photoUri,
                CropImageOptions(),
            ).apply {
                setAspectRatio(
                    aspectRatioX = screenX,
                    aspectRatioY = screenY,
                )
                setOutputUri(fileUri)
            }
            if (photoUri != null) {
                imageCropLauncher.launch(cropOptions)
            }
        }

    Box(modifier = modifier) {
        HomeScreen(
            modifier = modifier,
            homeScreenUiState = homeScreenUiState,
            onHomeMenuClick = { homeMenu ->
                when (homeMenu) {
                    HomeMenu.Settings -> {
                        onSettingClick()
                    }
                    HomeMenu.Edit -> {
                        isShowHomeEditContentDialog = true
                    }
                    HomeMenu.Save -> {
                        viewModel.saveLockScreen(context = context)
                    }
                    HomeMenu.Clear -> {
                    }
                }
            },
        )

        if (isShowHomeEditContentDialog) {
            HomeEditContentDialog(
                modifier = Modifier.fillMaxWidth(),
                onClick = { editType ->
                    when (editType) {
                        HomeEditType.Background -> {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        HomeEditType.TimeFormat -> {
                            isShowHomeEditTimeFormatDialog = true
                        }

                        else -> {}
                    }
                },
                onDismiss = { isShowHomeEditContentDialog = false },
            )
        }

        if (isShowHomeEditTimeFormatDialog) {
            HomeEditTimeFormatDialog(
                modifier = Modifier.fillMaxWidth(),
                selectedTimeFormat = (homeScreenUiState as? HomeScreenUiState.Success)?.lockScreen?.timeFormat,
                onClick = viewModel::setTimeFormat,
                onDismiss = { isShowHomeEditTimeFormatDialog = false },
            )
        }
    }
}
