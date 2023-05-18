package com.knocklock.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.lockscreen.SaveLockScreenCase
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.domain.model.TimeFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.locks.Lock
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLockScreenUseCase: GetLockScreenUseCase,
    private val saveLockScreenCase: SaveLockScreenCase
) : ViewModel() {
    private val initHomeMenuList = listOf(HomeMenu.SETTING, HomeMenu.EDIT, HomeMenu.SAVE)

    private val homeMenuList = MutableStateFlow(initHomeMenuList)

    private val tmpHomeScreen: MutableStateFlow<TmpScreenState> =
        MutableStateFlow(TmpScreenState.None)

    private val savedHomeScreenState: StateFlow<LockScreen> = getLockScreenUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LockScreen()
        )

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

    fun saveLockScreen() {
        viewModelScope.launch {
            val value = tmpHomeScreen.value
            if (value is TmpScreenState.Custom) {
                saveLockScreenCase(value.screen)
            }
        }
    }

    fun setWallPaper(uri: String) {
        viewModelScope.launch {
            tmpHomeScreen.update { state ->
                val background = LockScreenBackground.LocalImage(uri)
                if (state is TmpScreenState.Custom) {
                    state.copy(state.screen.copy(background = background))
                } else {
                    TmpScreenState.Custom(
                        screen = savedHomeScreenState.value.copy(background = background)
                    )
                }
            }
        }
    }

    fun setTimeFormat(format: TimeFormat) {
        viewModelScope.launch {
            tmpHomeScreen.update { state ->
                if (state is TmpScreenState.Custom) {
                    state.copy(state.screen.copy(timeFormat = format))
                } else {
                    TmpScreenState.Custom(
                        screen = savedHomeScreenState.value.copy(timeFormat = format)
                    )
                }
            }
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

    data class Custom(
        val screen: LockScreen
    ) : TmpScreenState
}