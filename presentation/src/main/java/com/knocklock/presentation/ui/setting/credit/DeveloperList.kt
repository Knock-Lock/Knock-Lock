package com.knocklock.presentation.ui.setting.credit

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R

@Composable
fun DeveloperList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Spacer(modifier.padding(20.dp))
        DeveloperItem(
            developer = Developer(
                stringResource(id = R.string.daq),
                painterResource(id = R.drawable.hyunkuk),
                stringResource(id = R.string.daq_comment)
            )
        )
        DeveloperItem(
            developer = Developer(
                stringResource(id = R.string.jaeryo),
                painterResource(id = R.drawable.minuk),
                stringResource(id = R.string.jaeryo_comment)
            )
        )
        DeveloperItem(
            developer = Developer(
                stringResource(id = R.string.hence),
                painterResource(id = R.drawable.hyunsu),
                stringResource(id = R.string.hence_comment)
            )
        )

    }
}