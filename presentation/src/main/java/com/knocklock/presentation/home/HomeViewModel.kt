package com.knocklock.presentation.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.domain.model.TimeFormat
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.lockscreen.SaveLockScreenCase
import com.knocklock.presentation.home.menu.HomeMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLockScreenUseCase: GetLockScreenUseCase,
    private val saveLockScreenCase: SaveLockScreenCase,
) : ViewModel() {
    private val initHomeMenuList = listOf(HomeMenu.Settings, HomeMenu.Edit, HomeMenu.Save)

    private val homeMenuList = MutableStateFlow(initHomeMenuList)

    private val tmpHomeScreen: MutableStateFlow<TmpScreenState> =
        MutableStateFlow(TmpScreenState.None)

    private val savedHomeScreenState: StateFlow<LockScreen> = getLockScreenUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LockScreen(),
        )

    val homeScreenUiState = combine(
        homeMenuList,
        tmpHomeScreen,
        savedHomeScreenState,
    ) { menuList, tmpScreen, savedScreen ->
        HomeScreenUiState.Success(
            menuList = menuList.toImmutableList(),
            lockScreen = if (tmpScreen is TmpScreenState.Custom) tmpScreen.screen else savedScreen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading,
    )

    fun saveLockScreen(context: Context) {
        viewModelScope.launch {
            val value = tmpHomeScreen.value
            if (value is TmpScreenState.Custom) {
                val uri = Uri.parse((value.screen.background as LockScreenBackground.LocalImage).imageUri)
                if (uri.path == null) {
                    return@launch
                } else {
                    val specificFile = File(uri.path).name
                    val fileNames = context.filesDir.list()
                    fileNames?.let { _ ->
                        var index = 0
                        while (index != fileNames.size) {
                            if (fileNames[index] != specificFile) {
                                val deleted = File(context.filesDir, fileNames[index]).delete()
                                if (deleted) {
                                    println("로그: File '${fileNames[index]}' has been removed.")
                                } else {
                                    println("로그: Failed to remove file '${fileNames[index]}'.")
                                }
                            }
                            index++
                        }
                    }
                    saveLockScreenCase(value.screen)
                }
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
                    println("로그 set: ${background.imageUri}")
                    TmpScreenState.Custom(
                        screen = savedHomeScreenState.value.copy(background = background),
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
                        screen = savedHomeScreenState.value.copy(timeFormat = format),
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
        val menuList: ImmutableList<HomeMenu>,
    ) : HomeScreenUiState
}

sealed interface TmpScreenState {
    object None : TmpScreenState

    data class Custom(
        val screen: LockScreen,
    ) : TmpScreenState
}
