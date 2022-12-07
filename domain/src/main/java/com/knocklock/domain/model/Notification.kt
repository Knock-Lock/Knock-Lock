package com.knocklock.domain.model

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:21 PM
 */
data class Notification(
    val id: Int,
    val title: String?,
    val text: String?,
    val subText: String?
//    val icon icon 을 넣으면 domain layer에서 android 의존성을 가지게 됌.
) {
    companion object {
        val EMPTY = Notification(
            id = 0,
            title = null,
            text = null,
            subText = null
        )
    }
}
