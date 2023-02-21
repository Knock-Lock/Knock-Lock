package com.knocklock.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.presentation.home.menu.HomeMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLockScreenUseCase: GetLockScreenUseCase
) : ViewModel() {
    private val initHomeMenuList = listOf(HomeMenu.SETTING, HomeMenu.TMP)

    private val homeMenuList = MutableStateFlow(initHomeMenuList)

    val homeScreenUiState = getLockScreenUseCase()
        .combine(homeMenuList) { lockScreen, menuList ->
            HomeScreenUiState.Success(
                lockScreen = lockScreen,
                menuList = menuList.toImmutableList()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenUiState.Loading,
        )
}

sealed interface HomeScreenUiState {
    object Loading: HomeScreenUiState

    data class Success(
        val lockScreen: LockScreen,
        val menuList: ImmutableList<HomeMenu>
    ): HomeScreenUiState
}