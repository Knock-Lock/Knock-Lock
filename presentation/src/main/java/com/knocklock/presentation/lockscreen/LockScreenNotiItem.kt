package com.knocklock.presentation.lockscreen

import android.app.PendingIntent
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.knocklock.presentation.ui.theme.KnockLockTheme

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:06 PM
 */

@Immutable
data class Notification(
    val id: String = "",
    val drawable: Drawable? = null,
    val appTitle: String = "",
    val notiTime: String = "",
    val title: String = "",
    val content: String = "",
    val intent: PendingIntent? = null
)

@Composable
fun GroupLockNotiItem(
    modifier: Modifier = Modifier,
    notificationList: List<Notification>
) {
    val notification = notificationList[0]
    var clickableState by remember { mutableStateOf(false) }
    var expandState by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(notificationList.size) {
        clickableState = notificationList.size >= 2
    }

    Column(
        modifier = Modifier.clickable(enabled = clickableState) {
            expandState = !expandState
        }
    ) {
        Box {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LockNotiTop(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(top = 4.dp),
                    drawable = notification.drawable,
                    appTitle = notification.appTitle,
                    time = notification.notiTime
                )
                LockNotiContent(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 4.dp)
                        .wrapContentHeight(),
                    title = notification.title,
                    content = notification.content
                )
            }
            if (clickableState) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 5.dp),
                    imageVector = if (expandState)Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(visible = !expandState) {
            Column {
                if (notificationList.size == 2) {
                    MoreNotifcation(modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth().height(7.dp))
                } else if (notificationList.size >= 3) {
                    MoreNotifcation(modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth().height(7.dp))
                    MoreNotifcation(modifier = Modifier.padding(horizontal = 35.dp).fillMaxWidth().height(5.dp))
                }
            }
        }
        AnimatedVisibility(visible = expandState) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (index in 1 until notificationList.size) {
                    LockNotiItem(
                        modifier = Modifier.background(color = Color(0xFFFAFAFA).copy(alpha = 0.95f), shape = RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)),
                        notification = notificationList[index]
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun MoreNotifcation(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(color = Color(0xFFFAFAFA).copy(alpha = 0.9f), shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)).clip(shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
    ) {}
}

@Composable
fun LockNotiItem(
    modifier: Modifier = Modifier,
    notification: Notification
) {
    Column {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LockNotiTop(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 4.dp),
                drawable = notification.drawable,
                appTitle = notification.appTitle,
                time = notification.notiTime
            )
            LockNotiContent(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 4.dp)
                    .wrapContentHeight(),
                title = notification.title,
                content = notification.content
            )
        }
    }
}

@Composable
fun LockNotiTop(
    modifier: Modifier = Modifier,
    drawable: Drawable?,
    appTitle: String,
    time: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (drawable != null) {
                Image(
                    modifier = Modifier.size(10.dp),
                    painter = rememberDrawablePainter(drawable = drawable),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = appTitle,
                fontSize = 10.sp
            )
        }
        Text(
            text = time,
            fontSize = 10.sp
        )
    }
}

@Composable
fun LockNotiContent(
    modifier: Modifier = Modifier,
    title: String,
    content: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            title,
            fontWeight = FontWeight.W700
        )
        Text(
            content,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

