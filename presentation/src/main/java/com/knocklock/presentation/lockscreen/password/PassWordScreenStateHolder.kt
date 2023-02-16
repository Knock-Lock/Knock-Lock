package com.knocklock.presentation.lockscreen.password

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.knocklock.domain.usecase.setting.GetUserUseCase
import com.knocklock.presentation.lockscreen.UseCaseEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @Created by 김현국 2023/01/11
 * @Time 5:24 PM
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface UseCaseEntryPoint {
    fun getUserUseCase(): GetUserUseCase
}

@Stable
class PassWordScreenStateHolder(
    context: Context,
    private val returnLockScreen: () -> Unit,
    private val unLockPassWordScreen: () -> Unit,
    private val scope: CoroutineScope
) {
    private val useCaseEntryPoint = EntryPointAccessors.fromApplication(
        context,
        UseCaseEntryPoint::class.java
    )

    val passWordState = mutableStateListOf<PassWord>().apply {
        repeat(6) {
            add(PassWord(""))
        }
    }

    var insertPassWordIndex by mutableStateOf(0)
    var removePassWordIndex by mutableStateOf(0)

    var isPlaying by mutableStateOf(false)

    fun updatePassWordState(passWord: String) {
        if (insertPassWordIndex <= 5) {
            passWordState[insertPassWordIndex] = passWordState[insertPassWordIndex].copy(number = passWord)
            if (insertPassWordIndex != 5) {
                insertPassWordIndex += 1
            }
            removePassWordIndex = if (insertPassWordIndex == 5) {
                insertPassWordIndex
            } else {
                insertPassWordIndex - 1
            }
            if (insertPassWordIndex == 6) {
                scope.launch {
                    useCaseEntryPoint.getUserUseCase().invoke().collect { user ->
                        val savedPassWord = user.password
                        val inputPassWord = passWordState.map {
                            it.number
                        }.joinToString("") { it }
                        if (savedPassWord == inputPassWord) {
                            unLockPassWordScreen()
                        }
                    }
                }
            }
        }
    }

    fun removePassWord() {
        passWordState[removePassWordIndex] = passWordState[removePassWordIndex].copy(number = "")
        if (insertPassWordIndex != 0) {
            insertPassWordIndex -= 1
            if (removePassWordIndex != 0) {
                removePassWordIndex -= 1
            } else {
                returnLockScreen()
            }
        } else {
            if (removePassWordIndex == 0) {
                returnLockScreen()
            }
        }
    }

    fun getPassWordList(): List<PassWord> {
        return PassWord.getPassWordList()
    }
    companion object {
        fun Saver(
            context: Context,
            returnLockScreen: () -> Unit,
            unLockPassWordScreen: () -> Unit,
            coroutineScope: CoroutineScope
        ) = Saver<PassWordScreenStateHolder, Any>(
            save = { listOf(it.passWordState, it.insertPassWordIndex, it.removePassWordIndex) },
            restore = { PassWordScreenStateHolder(returnLockScreen = returnLockScreen, unLockPassWordScreen = unLockPassWordScreen, context = context, scope = coroutineScope) }
        )
    }
}

@Composable
fun rememberPassWordScreenState(
    context: Context = LocalContext.current,
    returnLockScreen: () -> Unit,
    unLockPassWordScreen: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = rememberSaveable(
    saver = PassWordScreenStateHolder.Saver(
        context = context,
        returnLockScreen = returnLockScreen,
        unLockPassWordScreen = unLockPassWordScreen,
        coroutineScope = coroutineScope
    )
) {
    PassWordScreenStateHolder(
        context = context,
        returnLockScreen = returnLockScreen,
        unLockPassWordScreen = unLockPassWordScreen,
        scope = coroutineScope
    )
}
