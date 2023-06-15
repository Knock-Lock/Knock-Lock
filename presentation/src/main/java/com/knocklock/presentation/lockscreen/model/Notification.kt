package com.knocklock.presentation.lockscreen.model

import android.app.PendingIntent
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Immutable

/**
 * @Created by 김현국 2023/03/07
 */

@Immutable
data class Notification(
    val groupKey: String,
    val id: String = "",
    val drawable: Drawable? = null,
    val appTitle: String = "",
    val postedTime: Long,
    val notiTime: String = "",
    val title: String = "",
    val content: String = "",
    val isClearable: Boolean = false,
    val intent: PendingIntent? = null,
    val packageName: String? = null,
) {
    companion object {
        val default = Notification(
            groupKey = "",
            id = "",
            appTitle = "낙낙",
            postedTime = System.currentTimeMillis(),
            notiTime = "",
            title = "Sample",
            content = "Sample Text",
            isClearable = false,
        )
    }
}

data class RemovedGroupNotification(
    val key: String,
    val type: RemovedType,
    val removedNotifications: List<Notification>,
)

enum class RemovedType {
    Recent, Old
}
