package com.knocklock.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.lockscreen.SaveWallPaperUseCase
import com.knocklock.presentation.home.menu.HomeMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLockScreenUseCase: GetLockScreenUseCase,
    private val saveWallPaperUseCase: SaveWallPaperUseCase
) : ViewModel() {
    private val initHomeMenuList = listOf(HomeMenu.SETTING, HomeMenu.TMP)

    private val homeMenuList = MutableStateFlow(initHomeMenuList)

    private val tmpHomeScreen: MutableStateFlow<TmpScreenState> =
        MutableStateFlow(TmpScreenState.None)

    private val savedHomeScreenState = getLockScreenUseCase()

    val homeScreenUiState = combine(
        homeMenuList,
        tmpHomeScreen,
        savedHomeScreenState
    ) { menuList, tmpScreen, savedScreen ->
        HomeScreenUiState.Success(
            menuList = menuList.toImmutableList(),
            lockScreen = if (tmpScreen is TmpScreenState.Custom) tmpScreen.screen else savedScreen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )

    fun saveWallPaper(uri: String) {
        viewModelScope.launch {
            saveWallPaperUseCase(uri)
        }
    }
}

sealed interface HomeScreenUiState {
    object Loading : HomeScreenUiState

    data class Success(
        val lockScreen: LockScreen,
        val menuList: ImmutableList<HomeMenu>
    ) : HomeScreenUiState
}

sealed interface TmpScreenState {
    object None : TmpScreenState

    data class Custom(val screen: LockScreen) : TmpScreenState
}