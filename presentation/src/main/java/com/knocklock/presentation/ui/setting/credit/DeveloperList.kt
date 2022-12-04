package com.knocklock.presentation.ui.setting.credit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R

@Composable
fun DeveloperList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DeveloperItem(
            developer = Developer(
                "Daq",
                painterResource(id = R.drawable.hyunkuk),
                "안녕하슈"
            )
        )
        DeveloperItem(
            developer = Developer(
                "Jaeryo",
                painterResource(id = R.drawable.minuk),
                "안녕하슈"
            )
        )
        DeveloperItem(
            developer = Developer(
                "Hence",
                painterResource(id = R.drawable.hyunsu),
                "안녕하슈"
            )
        )

    }
}


@Preview
@Composable
private fun PreviewDeveloperList() {
    DeveloperList()
}