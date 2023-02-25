package com.knocklock.domain.model

/**
 * @Created by 김현국 2023/02/24
 */
data class Notification(
    val id: String,
    val groupKey: String,
    val appTitle: String,
    val postTime: String,
    val packageName: String,
    val title: String,
    val content: String,
    val isClearable: Boolean,
    val intent: String
)
