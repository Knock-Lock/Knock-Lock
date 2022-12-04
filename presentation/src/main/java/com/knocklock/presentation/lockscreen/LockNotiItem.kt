package com.knocklock.presentation.lockscreen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.ui.theme.KnockLockTheme

/**
 * @Created by 김현국 2022/12/02
 * @Time 3:06 PM
 */

data class Notification(
    val icon: Bitmap?,
    val appTitle: String,
    val notiTime: String,
    val title: String,
    val content: String
) {
    companion object {
        val Test = Notification(
            icon = null,
            appTitle = "Kakao",
            notiTime = "Now",
            title = "KnockLock app 개발중",
            content = "이거 너무나 재미가 있는걸~"
        )
    }
}

@Composable
fun LockNotiItem(
    modifier: Modifier = Modifier,
    notification: Notification
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        LockNotiTop(
            modifier = Modifier.padding(horizontal = 10.dp),
            icon = null,
            appTitle = notification.appTitle,
            time = notification.notiTime
        )
        LockNotiContent(
            modifier = Modifier.padding(horizontal = 10.dp).wrapContentHeight(),
            title = notification.title,
            content = notification.content
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun LockNotiTop(
    modifier: Modifier = Modifier,
    icon: Painter?,
    appTitle: String,
    time: String
) {
    Row(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box( // 추후 Image로 변경
                modifier = Modifier.size(10.dp).background(
                    color = Color.Green
                )
            )
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

@Preview
@Composable
fun PreviewLockNotiItem() {
    KnockLockTheme {
        val color = Color(red = 0xCC, blue = 0xCC, green = 0xCC)
        LockNotiItem(
            modifier = Modifier.width(200.dp).height(70.dp).background(
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
                .clip(RoundedCornerShape(4.dp)),
            notification = Notification.Test
        )
    }
}
