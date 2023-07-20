package com.knocklock.domain.model

import kotlinx.serialization.Serializable

/**
 * @Created by 김현국 2023/03/06
 */
@Serializable
data class Notification(
    val id: String = "",
    val packageName: String = "",
    val appTitle: String = "",
    val postedTime: Long,
    val title: String = "",
    val content: String = "",
    val isClearable: Boolean = false,
    val groupKey: String = "",
    val intent: String? = null,
)
