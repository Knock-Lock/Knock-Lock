package com.knocklock.presentation.setting.password

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PasswordSettingRoute(
    onSuccessChangePassword: () -> Unit,
    onClickBackButton: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordInputViewModel = hiltViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.onSuccessUpdatePassword.collectLatest {
                onSuccessChangePassword()
            }
        }
    }

    PasswordSettingScreen(
        modifier = modifier.fillMaxSize(),
        state = viewModel.passwordInputState,
        onClickTextButton = viewModel::onClickTextButton,
        onClickAction = viewModel::onClickKeyboardAction,
        onClickBackButton = onClickBackButton,
        onWiggleAnimationEnded = viewModel::onWiggleAnimationEnded
    )
}