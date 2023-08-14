package com.knocklock.presentation.lockscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
            modifier = Modifier.fillMaxWidth(0.5f),
            text = groupTitle,
            color = Color.White,
            fontSize = 21.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExpandButton(
                modifier = Modifier.padding(4.dp).height(28.dp).width(100.dp).clickable {
                    onDisableExpand()
                }.background(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50.dp),
                ),
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
}

@Composable
fun ExpandButton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Canvas(
            modifier = Modifier.size(24.dp),
        ) {
            drawLine(
                color = Color(0x99292D32),
                strokeWidth = 3.dp.toPx(),
                start = Offset(x = size.width / 24 * 18, y = size.height / 5 * 3),
                end = Offset(x = size.width / 2, y = size.height / 24 * 9),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color(0x99292D32),
                strokeWidth = 3.dp.toPx(),
                start = Offset(x = size.width / 24 * 6, y = size.height / 5 * 3),
                end = Offset(x = size.width / 2, y = size.height / 24 * 9),
                cap = StrokeCap.Round,
            )
        }
        Text(
            text = "간략히 보기",
            fontSize = 12.sp,
            color = Color(0x99292D32),
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
            modifier = Modifier.size(93.dp),
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
