package com.knocklock.presentation

import androidx.lifecycle.ViewModel
import com.knocklock.domain.usecase.setting.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    val isLockActivated = getUserUseCase().map { it.isLockActivated }
}