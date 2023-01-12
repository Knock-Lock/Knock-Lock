package com.knocklock.presentation.lockscreen.password

/**
 * @Created by 김현국 2023/01/08
 * @Time 4:06 PM
 */
data class PassWord(
    val number: String,
    val subText: String? = ""
) {
    companion object {
        fun getPassWordList(): List<PassWord> {
            return listOf(
                PassWord("1", ""),
                PassWord("2", "ABC"),
                PassWord("3", "DEF"),
                PassWord("4", "GHI"),
                PassWord("5", "JKL"),
                PassWord("6", "MNO"),
                PassWord("7", "PQRS"),
                PassWord("8", "TUV"),
                PassWord("9", "WXYZ"),
                PassWord("", ""),
                PassWord("0", ""),
                PassWord("", "")
            )
        }
    }
}
