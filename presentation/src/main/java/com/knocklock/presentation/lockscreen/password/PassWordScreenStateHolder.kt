package com.knocklock.presentation.lockscreen.password

import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * @Created by 김현국 2023/01/11
 * @Time 5:24 PM
 */

@Stable
class PassWordScreenStateHolder(
    configuration: Configuration,
    private val removePassWordScreen: () -> Unit
) {

    val passWordSpace = 50.dp
    val contentPadding = passWordSpace / 2
    private val screenWidthDp = configuration.screenWidthDp.dp - passWordSpace * 3
    val circlePassWordNumberSize = screenWidthDp / 3
    val passWordState = mutableStateListOf(
        PassWord(""),
        PassWord(""),
        PassWord(""),
        PassWord(""),
        PassWord(""),
        PassWord("")
    )

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
}

@Composable
fun rememberPassWordScreenState(
    configuration: Configuration = LocalConfiguration.current,
    removePassWordScreen: () -> Unit
) = remember {
    PassWordScreenStateHolder(
        configuration,
        removePassWordScreen
    )
}
