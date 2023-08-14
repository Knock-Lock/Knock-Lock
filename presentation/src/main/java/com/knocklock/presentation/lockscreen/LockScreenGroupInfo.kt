package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * @Created by 김현국 2023/07/06
 */

@Composable
fun LockScreenGroupInfo(
    groupTitle: String,
    modifier: Modifier = Modifier,
    removeType: RemovedType,
    notifications: ImmutableList<Notification> = persistentListOf(),
    onRemoveNotification: (RemovedGroupNotification) -> Unit = {},
    onDisableExpand: () -> Unit = {},
) {
    val updatedNotifications by rememberUpdatedState(newValue = notifications)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.7f),
            text = groupTitle,
            color = Color.White,
            fontSize = 25.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        ExpandButton(
            modifier = Modifier.size(30.dp).clickable {
                onDisableExpand()
            },
        )
        RemoveButton(
            modifier = Modifier.size(30.dp).clickable {
                onRemoveNotification(
                    RemovedGroupNotification(
                        key = updatedNotifications[0].groupKey,
                        type = removeType,
                        removedNotifications = updatedNotifications,
                    ),
                )
            },
        )
    }
}

@Composable
fun ExpandButton(
    modifier: Modifier = Modifier,
) {
    
    Canvas(
        modifier = Modifier,
    ) {
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.4f),
        )

        drawLine(
            color = Color.Gray,
            strokeWidth = 5.dp.toPx(),
            start = Offset(x = size.width / 5 * 4, y = size.height / 5 * 3),
            end = Offset(x = size.width / 2, y = size.height / 3),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.Gray,
            strokeWidth = 5.dp.toPx(),
            start = Offset(x = size.width / 5 * 1, y = size.height / 5 * 3),
            end = Offset(x = size.width / 2, y = size.height / 3),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun RemoveButton(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier,
    ) {
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.4f),
        )
        drawLine(
            color = Color(0xFF292D32).copy(0.6f),
            strokeWidth = 3.dp.toPx(),
            start = Offset(x = size.width / 3, y = size.height / 3),
            end = Offset(x = size.width - size.width / 3, y = size.height - size.height / 3),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color(0xFF292D32).copy(0.6f),
            strokeWidth = 3.dp.toPx(),
            start = Offset(x = size.width / 3, y = size.height - size.height / 3),
            end = Offset(x = size.width - size.width / 3, y = size.height / 3),
            cap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRemoveButton() {
    KnockLockTheme {
        RemoveButton(
            modifier = Modifier.size(50.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExpandButton() {
    KnockLockTheme {
        ExpandButton(
            modifier = Modifier.size(50.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGroupInfo() {
    KnockLockTheme {
        LockScreenGroupInfo(
            groupTitle = "낙낙",
            removeType = RemovedType.Old,
        )
    }
}
