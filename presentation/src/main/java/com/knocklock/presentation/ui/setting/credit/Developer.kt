package com.knocklock.presentation.ui.setting.credit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            modifier = modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp)),
            painter = developer.profile,
            contentDescription = null,
        )
        Column(
            modifier = modifier.padding(start = 10.dp)
        ) {
            Text(text = developer.name)
            Spacer(modifier = modifier.padding(5.dp))
            Text(text = developer.comment)
        }
    }

}

@Preview
@Composable
private fun PreviewDeveloper() {
    DeveloperItem(
        developer = Developer(
            "Hence",
            painterResource(id = R.drawable.hyunsu),
            "안녕하세요"
        )
    )
}