package com.knocklock.domain.model

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:21 PM
 */
data class Notification(
    val id: Int = 0,
    val title: String = "",
    val text: String = "",
    val subText: String = ""
)
