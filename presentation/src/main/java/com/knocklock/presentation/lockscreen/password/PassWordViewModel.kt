package com.knocklock.presentation.lockscreen.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.usecase.setting.GetUserUseCase
import com.knocklock.presentation.constant.MAX_PASSWORD_LENGTH
import com.knocklock.presentation.lockscreen.password.Event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Created by 김현국 2023/05/17
 */
@HiltViewModel
class PassWordViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    val passWordState = mutableStateListOf<PassWord>().apply {
        repeat(MAX_PASSWORD_LENGTH) {
            add(PassWord(""))
        }
    }

    var isPlaying by mutableStateOf(false)

    private var insertPassWordIndex by mutableStateOf(0)
    private var removePassWordIndex by mutableStateOf(0)

    private val _eventState = MutableSharedFlow<Event>()
    val eventState = _eventState.asSharedFlow()

    fun updatePassWordState(passWord: String) {
        if (insertPassWordIndex <= MAX_PASSWORD_LENGTH) {
            passWordState[insertPassWordIndex] = passWordState[insertPassWordIndex].copy(number = passWord)
            if (insertPassWordIndex != MAX_PASSWORD_LENGTH) {
                insertPassWordIndex += 1
            }
            removePassWordIndex = if (insertPassWordIndex == MAX_PASSWORD_LENGTH - 1) {
                insertPassWordIndex
            } else {
                insertPassWordIndex - 1
            }
            if (insertPassWordIndex == MAX_PASSWORD_LENGTH) {
                viewModelScope.launch {
                    getUserUseCase().collect { user ->
                        val savedPassWord = user.password
                        val inputPassWord = passWordState.map {
                            it.number
                        }.joinToString("") { it }
                        if (savedPassWord == inputPassWord) {
                            _eventState.emit(UNLOCK)
                        } else {
                            resetPasswordIndex()
                        }
                    }
                }
            }
        }
    }

    private fun resetPasswordIndex() {
        viewModelScope.launch {
            insertPassWordIndex = 0
            removePassWordIndex = 0
            passWordState.replaceAll { PassWord("") }
            _eventState.emit(VIBRATE)
        }
    }

    fun removePassWord() {
        viewModelScope.launch {
            passWordState[removePassWordIndex] = passWordState[removePassWordIndex].copy(number = "")
            if (insertPassWordIndex != 0) {
                insertPassWordIndex -= 1
                if (removePassWordIndex != 0) {
                    removePassWordIndex -= 1
                } else {
                    _eventState.emit(RETURN)
                }
            } else {
                if (removePassWordIndex == 0) {
                    _eventState.emit(RETURN)
                }
            }
        }
    }

    fun getPassWordList(): List<PassWord> {
        return PassWord.getPassWordList()
    }

    fun resetState() {
        viewModelScope.launch {
            _eventState.emit(NOTHING)
        }
    }
}

enum class Event {
    RETURN, UNLOCK, NOTHING, VIBRATE
}
