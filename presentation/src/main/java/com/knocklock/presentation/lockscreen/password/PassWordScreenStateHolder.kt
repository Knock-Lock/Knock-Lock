package com.knocklock.presentation.lockscreen.password

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * @Created by 김현국 2023/01/11
 * @Time 5:24 PM
 */

@Stable
class PassWordScreenStateHolder(
    private val removePassWordScreen: () -> Unit
) {
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
        }
    }

    fun removePassWord() {
        passWordState[removePassWordIndex] = passWordState[removePassWordIndex].copy(number = "")
        if (insertPassWordIndex != 0) {
            insertPassWordIndex -= 1
            if (removePassWordIndex != 0) {
                removePassWordIndex -= 1
            } else {
                removePassWordScreen()
            }
        }
    }

    fun getPassWordList(): List<PassWord> {
        return PassWord.getPassWordList()
    }
    companion object {
        fun Saver(
            removePassWordScreen: () -> Unit
        ) = Saver<PassWordScreenStateHolder, Any>(
            save = { listOf(it.passWordState, it.insertPassWordIndex, it.removePassWordIndex) },
            restore = { PassWordScreenStateHolder(removePassWordScreen) }
        )
    }
}

@Composable
fun rememberPassWordScreenState(
    removePassWordScreen: () -> Unit
) = rememberSaveable(
    saver = PassWordScreenStateHolder.Saver(
        removePassWordScreen = removePassWordScreen
    )
) {
    PassWordScreenStateHolder(
        removePassWordScreen = removePassWordScreen
    )
}
