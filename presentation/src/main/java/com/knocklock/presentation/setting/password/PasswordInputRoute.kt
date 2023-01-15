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
fun PasswordInputRoute(
    modifier: Modifier = Modifier,
    viewModel: PasswordInputViewModel = hiltViewModel(),
    onSuccessChangePassword: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.onSuccessUpdatePassword.collectLatest {
                onSuccessChangePassword()
            }
        }
    }

    PasswordInputScreen(
        modifier = modifier.fillMaxSize(),
        state = viewModel.passwordInputState,
        onClickTextButton = viewModel::onClickTextButton,
        onClickAction = viewModel::onClickKeyboardAction,
    )
}