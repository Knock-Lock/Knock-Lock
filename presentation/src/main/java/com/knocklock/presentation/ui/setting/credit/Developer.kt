package com.knocklock.presentation.ui.setting.credit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R

data class Developer(
    val name: String,
    val profile: Painter,
    val comment: String
)

@Composable
fun DeveloperItem(
    modifier: Modifier = Modifier,
    developer: Developer
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
    ) {
        Image(
            modifier = modifier
                .size(110.dp)
                .clip(RoundedCornerShape(16.dp)),
            painter = developer.profile,
            contentDescription = null,

            )
        Column(
            modifier = modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = developer.name,
                fontSize = 22.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = modifier.padding(2.dp))
            Text(
                text = developer.comment,
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDeveloper() {
    DeveloperItem(
        developer = Developer(
            stringResource(id = R.string.daq),
            painterResource(id = R.drawable.hyunkuk),
            stringResource(id = R.string.daq_comment)
        )
    )
}