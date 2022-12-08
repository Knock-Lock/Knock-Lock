package com.knocklock.presentation.ui.setting.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.setting.SettingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBackPressedIconSelected: () -> Unit,
    onMenuSelected: () -> Unit,
    settingViewModel: SettingViewModel = viewModel()
) {
    Scaffold(
        topBar = { SettingHeader(modifier, onBackPressedIconSelected) },
    ) {
        Column(modifier.padding(it)) {
            Spacer(modifier.padding(20.dp))
            SettingBody(
                modifier,
                onMenuSelected,
                onCheckedChange = { settingViewModel.tmpChangeSwitchChecked(it) },
                settingViewModel.isActivated.collectAsState().value
            )
        }
    }
}

@Composable
private fun SettingBody(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean
) {
    Surface(
        modifier.fillMaxSize(),
        color = Color(0xffEFEEF3)
    ) {
        MenuList(modifier, onMenuSelected, onCheckedChange, checked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingHeader(
    modifier: Modifier = Modifier,
    onBackPressedIconSelected: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .size(24.dp),
                onClick = onBackPressedIconSelected
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.setting)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}


@Preview
@Composable
fun PreviewSettingScreen() {
    SettingScreen(onBackPressedIconSelected = {}, onMenuSelected = {})
}